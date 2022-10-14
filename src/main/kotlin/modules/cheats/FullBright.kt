package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client

@ClientModule
object FullBright : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.fullbright.name")
    override val description = Text.translatable("cheat.modid.fullbright.description")

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
            if (!it.hasStatusEffect(StatusEffects.NIGHT_VISION))
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

            val effect = it.getStatusEffect(StatusEffects.NIGHT_VISION)!!

            if (effect.duration < 10)
                effect.duration = 10
        }
    }

    override fun onDisable() {
        super.onDisable()

        Client.player?.let {
            val effect = it.getStatusEffect(StatusEffects.NIGHT_VISION)
            if (effect != null && effect.duration <= 10)
                it.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
    }
}