package modules.cheats

import Logger
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object SpeedMine : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.speedmine.name")
    override val description = TranslatableText("cheat.modid.speedmine.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.speedmine", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    var speedModifier: Float = 1.6f

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private fun onEnable() {
        Logger.info("Enabling speedmine...")

        Client.player?.sendMessage(Text.of("Enabling speedmine!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling speedmine...")

        Client.player?.sendMessage(Text.of("Disabling speedmine!"), false)
    }
}