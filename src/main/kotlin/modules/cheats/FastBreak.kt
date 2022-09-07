package modules.cheats

import Logger
import modules.Keybinded
import event.EventHandler
import events.world.TickEvent
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Direction
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object FastBreak : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf()

    override val name = TranslatableText("cheat.modid.fastbreak.name")
    override val description = TranslatableText("cheat.modid.fastbreak.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.fastbreak", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(TickEvent.Post::class)
    private fun breakBlockIfMining() {
        Client.interactionManager?.let { interactionManager ->
            if (!enabled || !interactionManager.isBreakingBlock) return

            if (interactionManager.currentBreakingProgress >= 1) return

            interactionManager.sendPlayerAction(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                interactionManager.currentBreakingPos!!,
                Direction.DOWN
            )
        }
    }

    private fun onEnable() {
        Logger.info("Enabling fastbreak...")

        Client.player?.sendMessage(Text.of("Enabling fastbreak!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling fastbreak...")

        Client.player?.sendMessage(Text.of("Disabling fastbreak!"), false)
    }
}