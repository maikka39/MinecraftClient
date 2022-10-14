package modules.cheats

import event.EventHandler
import events.world.SoundEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.FishingRodItem
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.DelayedAction
import utils.Global.Client
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingDoubleSliderCallbacks
import utils.options.OptionCallbacks.Companion.ValidatingIntSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object AutoFish : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.autofish.name")
    override val description = Text.translatable("cheat.modid.autofish.description")

        override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.autofish", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    private val reelInDelay = Option<Int>(
        "options.modid.autofish.reelInDelay.name",
        TooltipFactory.fromKey("options.modid.autofish.reelInDelay.description"),
        ValueTextFactory.simpleInt,
        ValidatingIntSliderCallbacks(1, 50),
        6,
    )

    private val castDelay = Option<Int>(
        "options.modid.autofish.castDelay.name",
        TooltipFactory.fromKey("options.modid.autofish.castDelay.description"),
        ValueTextFactory.simpleInt,
        ValidatingIntSliderCallbacks(1, 50),
        10,
    )

    private val range = Option<Double>(
        "options.modid.autofish.range.name",
        TooltipFactory.fromKey("options.modid.autofish.range.description"),
        ValueTextFactory.roundedDouble(1),
        ValidatingDoubleSliderCallbacks(1.0, 20.0, 0.5),
        10.0,
    )

    override fun onKeybindingPressed() {
        enabled = !enabled
    }


    @EventHandler(SoundEvent::class)
    private fun onSoundEvent(event: SoundEvent) {
        if (!enabled) return

        val soundInstance = event.soundInstance

        if (soundInstance.id != SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.id) return

        Client.player?.fishHook?.let { fishHook ->
            val fishHookLocation = Vec3d(fishHook.x, fishHook.y, fishHook.z)
            val soundInstanceLocation = Vec3d(soundInstance.x, soundInstance.y, soundInstance.z)

            if (fishHookLocation.distanceTo(soundInstanceLocation) <= range.value) {
                DelayedAction.register({ if (enabled) useFishingRod() }, reelInDelay.value)
                DelayedAction.register({ if (enabled) useFishingRod() }, reelInDelay.value + castDelay.value)
            }
        }
    }

    private fun useFishingRod() {
        Client.player?.let {
            if (it.mainHandStack.item !is FishingRodItem) return

            Client.interactionManager!!.interactItem(it, Hand.MAIN_HAND)
        }
    }
}