package modules.cheats

import Logger
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.Global.Client

object Reach : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.reach.distance",
            3.0,
            6.0,
            0.5f,
            { reach.toDouble() },
            { _, value: Double -> reach = value.toFloat() },
            ModSettingsListWidget.getDoubleLabel
        ),
    )

    override val name = TranslatableText("cheat.modid.reach.name")
    override val description = TranslatableText("cheat.modid.reach.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.reach", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    var reach: Float = 6f

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private fun onEnable() {
        Logger.info("Enabling reach...")

        Client.player?.sendMessage(Text.of("Enabling reach!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling reach...")

        Client.player?.sendMessage(Text.of("Disabling reach!"), false)
    }
}