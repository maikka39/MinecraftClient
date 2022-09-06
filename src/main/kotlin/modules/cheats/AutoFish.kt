package modules.cheats

import Logger
import event.EventHandler
import events.world.SoundEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.FishingRodItem
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.DelayedAction
import utils.Global.Client

object AutoFish : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.autofish.name")
    override val description = TranslatableText("cheat.modid.autofish.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.autofish", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    var reelInDelay: Int = 6
    var castDelay: Int = 10
    var range: Double = 10.0

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(SoundEvent::class)
    private fun onSoundEvent(event: SoundEvent) {
        if (!enabled) return

        val soundInstance = event.soundInstance

        if (soundInstance.id != SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.id)
            return

        Client.player?.fishHook?.let { fishHook ->
            val fishHookLocation = Vec3d(fishHook.x, fishHook.y, fishHook.z)
            val soundInstanceLocation = Vec3d(soundInstance.x, soundInstance.y, soundInstance.z)

            if (fishHookLocation.distanceTo(soundInstanceLocation) <= range) {
                DelayedAction.register({ if (enabled) useFishingRod() }, reelInDelay)
                DelayedAction.register({ if (enabled) useFishingRod() }, reelInDelay + castDelay)
            }
        }
    }

    private fun useFishingRod() {
        Client.player?.let {
            if (it.mainHandStack.item !is FishingRodItem)
                return

            Client.interactionManager!!.interactItem(it, Client.world!!, Hand.MAIN_HAND)
        }
    }

    private fun onEnable() {
        Logger.info("Enabling autofish...")

        Client.player?.sendMessage(Text.of("Enabling autofish!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling autofish...")

        Client.player?.sendMessage(Text.of("Disabling autofish!"), false)
    }
}