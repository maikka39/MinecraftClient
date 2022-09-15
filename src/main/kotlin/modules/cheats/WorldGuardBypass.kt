package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.TickEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.Packet
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import kotlin.math.abs

object WorldGuardBypass : Cheat("WorldGuardBypass"), Keybinded {
    override val name = TranslatableText("cheat.modid.worldguardbypass.name")
    override val description = TranslatableText("cheat.modid.worldguardbypass.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.worldguardbypass", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
        NoRotate.enabled = enabled
    }

    private fun getPlayerMoveVector(player: ClientPlayerEntity): Vec3d {
        val absPlayerPitch = abs(player.pitch)
        val pitch =
            if (absPlayerPitch < 10) 0f else if (absPlayerPitch > 80) player.pitch / absPlayerPitch * 90f else player.pitch

        val verticalInput = -pitch / 90.0
        val horizontalInput = 1.0 - abs(pitch) / 90.0
        val movementInput = Vec3d(0.0, verticalInput, horizontalInput)
        val d: Double = movementInput.lengthSquared()
        return if (d < 1.0E-7) {
            Vec3d.ZERO
        } else {
            val vec3d: Vec3d = (if (d > 1.0) movementInput.normalize() else movementInput)
            val f = MathHelper.sin(player.yaw * 0.017453292f)
            val g = MathHelper.cos(player.yaw * 0.017453292f)
            Vec3d(
                vec3d.x * g.toDouble() - vec3d.z * f.toDouble(),
                vec3d.y,
                vec3d.z * g.toDouble() + vec3d.x * f.toDouble()
            )
        }
    }

    private fun hasCollisions(player: ClientPlayerEntity, world: ClientWorld, offset: Vec3d): Boolean {
        return world.getBlockCollisions(
            null, player.boundingBox.offset(offset)
        ).iterator().hasNext()
    }

    private fun sendPacket(networkHandler: ClientPlayNetworkHandler, packet: Packet<*>) {
        myPackets.add(packet)
        networkHandler.sendPacket(packet)
    }

    private val myPackets: MutableList<Packet<*>> = mutableListOf()

    private var ticksTillFlyBypass = 30

    @EventHandler(TickEvent.Post::class)
    private fun onTick() {
        if (!enabled) return

        val speed = 0.02 // Max equals (1f / 256) == 0.0625

        Client.player?.let { player ->
            val world = Client.world!!

            if (--ticksTillFlyBypass < 1) {
                ticksTillFlyBypass = 30

                val isNearBlock = world.getBlockCollisions(
                    null, player.boundingBox.expand(0.0625)
                ).iterator().hasNext()

                if (!isNearBlock) {
                    sendPacket(
                        player.networkHandler,
                        PlayerMoveC2SPacket.Full(
                            player.x,
                            player.y - 0.03130,
                            player.z,
                            0f,
                            0f,
                            player.isOnGround
                        )
                    )
                }
                return@let
            }

            val moveVector = getPlayerMoveVector(player)

            val distanceMoved = player.pos.add(moveVector).squaredDistanceTo(player.pos)

            val ratio = if (distanceMoved > speed)
                (speed / distanceMoved)
            else
                1.0

            val newPosDiff = moveVector.multiply(ratio)

            for (i in 0 until 2) {
                val newPos = player.pos.add(newPosDiff)

                if (hasCollisions(player, world, newPosDiff))
                    break

                player.setPosition(newPos)

                sendPacket(
                    player.networkHandler,
                    PlayerMoveC2SPacket.Full(
                        newPos.x,
                        newPos.y,
                        newPos.z,
                        0f,
                        0f,
                        player.isOnGround
                    )
                )
            }

            sendPacket(
                player.networkHandler,
                PlayerMoveC2SPacket.Full(
                    player.x + 1000,
                    player.y + 1000,
                    player.z + 1000,
                    0f,
                    0f,
                    player.isOnGround
                )
            )
            return@let
        }
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendEvent(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return

        if (!myPackets.remove(event.packet)) {
            event.cancel()
            return
        }

        val packet = event.packet

        val x = (packet.x * 1000).toInt() % 10
        val z = (packet.z * 1000).toInt() % 10

        packet.x -= x * 0.001
        packet.z -= z * 0.001
    }
}