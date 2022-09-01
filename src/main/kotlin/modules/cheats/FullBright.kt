package modules.cheats

import Logger
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

class FullBright : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.fullbright.name")
    override val description = TranslatableText("cheat.modid.fullbright.description")
    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private fun onEnable() {
        Logger.info("Enabling fullbright...")

        Client.getPlayer()?.let {
            it.sendMessage(Text.of("Enabling fullbright!"), false)

            it.addStatusEffect(
                StatusEffectInstance(
                    StatusEffects.NIGHT_VISION,
                    Int.MAX_VALUE,
                    Int.MAX_VALUE,
                    false,
                    false,
                    false
                )
            )
        }
    }

    private fun onDisable() {
        Logger.info("Disabling fullbright...")

        Client.getPlayer()?.let {
            it.sendMessage(Text.of("Disabling fullbright!"), false)

            it.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
    }
}