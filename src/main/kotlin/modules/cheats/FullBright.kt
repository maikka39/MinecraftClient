package modules.cheats

import Logger
import event.EventHandler
import events.world.TickEvent
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

object FullBright : Cheat, Keybinded {
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

    @EventHandler(TickEvent.Post::class)
    private fun afterTick() {
        if (!enabled) return

        Client.player?.let {
            if (!it.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                it.addStatusEffect(
                    StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        10,
                        Int.MAX_VALUE,
                        false,
                        false,
                        false
                    )
                )
                println("Added effect")
            }

            val effect = it.getStatusEffect(StatusEffects.NIGHT_VISION)!!

            if (effect.duration < 10)
                effect.duration = 10
        }
    }

    private fun onEnable() {
        Logger.info("Enabling fullbright...")

        Client.player?.sendMessage(Text.of("Enabling fullbright!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling fullbright...")

        Client.player?.let {
            val effect = it.getStatusEffect(StatusEffects.NIGHT_VISION)
            if (effect != null && effect.duration <= 10)
                it.removeStatusEffect(StatusEffects.NIGHT_VISION)

            it.sendMessage(Text.of("Disabling fullbright!"), false)
        }
    }
}