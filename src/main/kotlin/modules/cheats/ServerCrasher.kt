package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.WorldEvent
import mixinterfaces.IPlayerMoveC2SPacket
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.NetworkHandler

@ClientModule
object ServerCrasher : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.servercrasher.name")
    override val description = Text.translatable("cheat.modid.servercrasher.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.servercrasher", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private var teleportId = -1

    @EventHandler(WorldEvent.Join::class)
    private fun onJoinWorld() {
        enabled = false
        teleportId = -1
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        packet as IPlayerMoveC2SPacket
        if (packet.isMine) return

        val player = Client.player!!

        packet.pitch = Float.MAX_VALUE
        packet.yaw = Float.MAX_VALUE
        resetPosition(player)
    }

    private fun resetPosition(player: ClientPlayerEntity) {
        sendPos(player, Vec3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE), player.yaw, player.pitch)
        player.networkHandler.sendPacket(TeleportConfirmC2SPacket(++teleportId))
    }

    private fun sendPos(player: ClientPlayerEntity, pos: Vec3d, yaw: Float, pitch: Float) {
        NetworkHandler.sendPacket(
            player.networkHandler,
            PlayerMoveC2SPacket.Full(pos.x, pos.y, pos.z, yaw, pitch, true)
        )
    }

    @EventHandler(PacketEvent.Receive::class)
    private fun onTeleportReceive(event: PacketEvent.Receive) {
        if (!enabled) return
        if (event.packet !is PlayerPositionLookS2CPacket) return
        val packet = event.packet
        val player = Client.player!!

        event.cancel()
        if (packet.teleportId < teleportId) return

        teleportId = packet.teleportId
        player.networkHandler.sendPacket(TeleportConfirmC2SPacket(packet.teleportId))
    }
}