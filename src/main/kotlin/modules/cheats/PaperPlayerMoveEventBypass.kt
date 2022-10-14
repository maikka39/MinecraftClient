package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.WorldEvent
import mixinterfaces.IPlayerMoveC2SPacket
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.Vec3d
import utils.Global
import utils.NetworkHandler
import utils.PositionDirection

abstract class PaperPlayerMoveEventBypass : Cheat() {
    private var teleportId = -1
    private var previousPosDir: PositionDirection =
        PositionDirection(Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE), Float.MAX_VALUE, Float.MAX_VALUE)
    private var tpPosToReceive = mutableListOf<Vec3d>()
    private var lastSentPos = Vec3d.ZERO
    private val speed = 0.003 // Max equals (1f / 256) == 0.00390625

    protected fun resetPosition(player: ClientPlayerEntity) {
        tpPosToReceive.add(lastSentPos)
        sendPos(player, Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE), player.yaw, player.pitch)
        player.networkHandler.sendPacket(TeleportConfirmC2SPacket(++teleportId))
    }

    protected fun sendPos(player: ClientPlayerEntity, pos: Vec3d, yaw: Float, pitch: Float) {
        println(pos.x)
        println(pos.y)
        println(pos.z)
        println(yaw)
        println(pitch)
        val packet = PlayerMoveC2SPacket.Full(pos.x, pos.y, pos.z, yaw, pitch, true)
        NetworkHandler.sendPacket(player.networkHandler, packet)
        lastSentPos = pos
    }

    protected abstract fun onPacket(
        player: ClientPlayerEntity,
        packetPos: PositionDirection,
        previousPosDir: PositionDirection,
        posDiff: Vec3d,
        increment: Vec3d,
        count: Int
    ): Boolean

    @EventHandler(PacketEvent.Send::class)
    private fun onPlayerMove(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        packet as IPlayerMoveC2SPacket
        if (packet.isMine) return

        val player = Global.Client.player!!

        val packetPos = PositionDirection(
            Vec3d(
                packet.getX(player.x),
                packet.getY(player.y),
                packet.getZ(player.z)
            ),
            packet.getYaw(player.yaw),
            packet.getPitch(player.pitch)
        )

        if (previousPosDir.pos.x == Double.MAX_VALUE) {
            previousPosDir = packetPos
            return
        }

        val posDiff = packetPos.pos.subtract(previousPosDir.pos)
        val dist = packetPos.pos.squaredDistanceTo(previousPosDir.pos)

        val count = (dist / speed).toInt()

        val increment = posDiff.multiply(1.0 / count.coerceAtLeast(2))

        if (!onPacket(player, packetPos, previousPosDir, posDiff, increment, count)) {
            previousPosDir = packetPos
            return
        }

        event.cancel()
        previousPosDir = packetPos
    }

    @EventHandler(WorldEvent.Join::class)
    private fun onJoinWorld() {
        enabled = false
        teleportId = -1
        previousPosDir = PositionDirection(
            Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
            Float.MAX_VALUE,
            Float.MAX_VALUE
        )
        lastSentPos = Vec3d.ZERO
    }

    @EventHandler(PacketEvent.Receive::class)
    private fun onTeleportReceive(event: PacketEvent.Receive) {
        if (!enabled) return
        if (event.packet !is PlayerPositionLookS2CPacket) return
        val packet = event.packet
        val player = Global.Client.player!!
        val packetPos = Vec3d(packet.x, packet.y, packet.z)

        val expectedPos = tpPosToReceive.removeFirstOrNull()
        if (expectedPos == null || packetPos.squaredDistanceTo(expectedPos) > 0.01) {
            tpPosToReceive.clear()
            teleportId = packet.teleportId
            return
        }

        event.cancel()
        if (packet.teleportId < teleportId) return

        teleportId = packet.teleportId
        player.networkHandler.sendPacket(TeleportConfirmC2SPacket(packet.teleportId))
    }
}