package modules.cheats

import Logger
import event.EventHandler
import events.world.TickEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.Global.Client

object Spider : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.spider.climbingSpeed",
            0.05,
            1.0,
            0.05f,
            { climbingSpeed },
            { _, value: Double -> climbingSpeed = value },
            ModSettingsListWidget.getDoubleLabel
        ),
    )

    override val name = TranslatableText("cheat.modid.spider.name")
    override val description = TranslatableText("cheat.modid.spider.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.spider", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    var climbingSpeed = 0.2

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(TickEvent.Post::class)
    private fun afterTick() {
        if (!enabled) return

        Client.player?.let {
            if (!it.horizontalCollision) return

            if (it.velocity.y >= climbingSpeed) return

            it.velocity = Vec3d(it.velocity.x, climbingSpeed, it.velocity.z)
        }
    }

    private fun onEnable() {
        Logger.info("Enabling spider...")

        Client.player?.sendMessage(Text.of("Enabling spider!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling spider...")

        Client.player?.sendMessage(Text.of("Disabling spider!"), false)
    }
}