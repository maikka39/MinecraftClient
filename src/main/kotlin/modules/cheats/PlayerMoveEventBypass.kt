package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.WorldEvent
import mixinterfaces.IPlayerMoveC2SPacket
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.NetworkHandler
import utils.PositionDirection

object PlayerMoveEventBypass : Cheat("PlayerMoveEventBypass"), Keybinded {
    override val name = TranslatableText("cheat.modid.playermoveeventbypass.name")
    override val description = TranslatableText("cheat.modid.playermoveeventbypass.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.playermoveeventbypass", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
        NoRotate.enabled = enabled
    }

    private var teleportId = -1
    private var previousPosDir: PositionDirection =
        PositionDirection(Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE), Float.MAX_VALUE, Float.MAX_VALUE)
    private var tpPosToReceive = mutableListOf<Vec3d>()
    private var lastSentPos = Vec3d.ZERO
    private val speed = 0.003 // Max equals (1f / 256) == 0.00390625

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

    @EventHandler(PacketEvent.Send::class)
    private fun onPlayerMove(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        packet.pitch = 0f
        packet.yaw = 0f

        packet as IPlayerMoveC2SPacket
        if (packet.isMine) return

        val player = Client.player!!

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

        if (count < 1) return

        val increment = posDiff.multiply(1.0 / count.coerceAtLeast(2))

        for (i in 1..count) {
            val newPos = previousPosDir.pos.add(increment.multiply(i.toDouble()))

            sendPos(
                player,
                newPos,
                previousPosDir.yaw,
                previousPosDir.pitch,
            )
            resetPosition(player)
        }

        sendPos(player, packetPos.pos, packetPos.yaw, packetPos.pitch)
        resetPosition(player)

        event.cancel()
        previousPosDir = packetPos
    }

    private fun resetPosition(player: ClientPlayerEntity) {
        tpPosToReceive.add(lastSentPos)
        sendPos(player, Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE), player.yaw, player.pitch)
        player.networkHandler.sendPacket(TeleportConfirmC2SPacket(++teleportId))
    }

    private fun sendPos(player: ClientPlayerEntity, pos: Vec3d, yaw: Float, pitch: Float) {
        val packet = PlayerMoveC2SPacket.Full(pos.x, pos.y, pos.z, yaw, pitch, true)
        NetworkHandler.sendPacket(player.networkHandler, packet)
        lastSentPos = pos
    }

    @EventHandler(PacketEvent.Receive::class)
    private fun onTeleportReceive(event: PacketEvent.Receive) {
        if (!enabled) return
        if (event.packet !is PlayerPositionLookS2CPacket) return
        val packet = event.packet
        val player = Client.player!!
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