package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client

@ClientModule
object Blink : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.blink.name")
    override val description = Text.translatable("cheat.modid.blink.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.blink", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private val packets: MutableList<PlayerMoveC2SPacket> = mutableListOf()

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        event.cancel()
        val packet = event.packet

        val prev = packets.lastOrNull()

        if (prev != null &&
            packet.isOnGround == prev.isOnGround &&
            packet.getYaw(-1f) == prev.getYaw(-1f) &&
            packet.getPitch(-1f) == prev.getPitch(-1f) &&
            packet.getX(-1.0) == prev.getX(-1.0) &&
            packet.getY(-1.0) == prev.getY(-1.0) &&
            packet.getZ(-1.0) == prev.getZ(-1.0)
        ) return

        packets.add(packet)
    }

    override fun onDisable() {
        super.onDisable()
        packets.forEach(Client.player!!.networkHandler::sendPacket)
        packets.clear()
    }
}