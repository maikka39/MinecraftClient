package modules.cheats

import Logger
import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object NoFall : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf()

    override val name = TranslatableText("cheat.modid.nofall.name")
    override val description = TranslatableText("cheat.modid.nofall.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.nofall", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private fun onEnable() {
        Logger.info("Enabling nofall mode...")

        Client.player?.sendMessage(Text.of("Enabling nofall!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling nofall mode...")

        Client.player?.sendMessage(Text.of("Disabling nofall!"), false)
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet
        val player = Client.player!!

        if (player.velocity.y >= -0.08)
            return

        packet.onGround = true
    }
}