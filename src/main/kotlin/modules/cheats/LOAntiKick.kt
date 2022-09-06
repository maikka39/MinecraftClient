package modules.cheats

import Logger
import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object LOAntiKick : Cheat, Keybinded {
    override var enabled = true
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.loantikick.name")
    override val description = TranslatableText("cheat.modid.loantikick.description")
    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.loantikick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        val x = (packet.x * 1000).toInt() % 10
        val z = (packet.z * 1000).toInt() % 10

        packet.x -= x * 0.001
        packet.z -= z * 0.001
    }

    private fun onEnable() {
        Logger.info("Enabling loantikick...")

        Client.player?.sendMessage(Text.of("Enabling loantikick!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling loantikick...")

        Client.player?.sendMessage(Text.of("Disabling loantikick!"), false)
    }
}