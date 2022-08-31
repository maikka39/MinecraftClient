package cheats

import Logger
import cheats.interfaces.Cheat
import cheats.interfaces.Keybinded
import event.EventHandler
import events.world.TickEvent
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Direction
import org.lwjgl.glfw.GLFW
import utils.Global.Client

class FastBreak : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.fastbreak.name")
    override val description = TranslatableText("cheat.modid.fastbreak.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.fastbreak", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.modid.cheat"
        )
    )!!

    @EventHandler(TickEvent.Post::class)
    private fun breakBlockIfMining() {
        Client.getInteractionManager()?.let { interactionManager ->
            if (!enabled || !interactionManager.isBreakingBlock()) return
            println("Test 2")

            if (interactionManager.getCurrentBreakingProgress() >= 1) return
            println("Test 3")

            val action = PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
            println("Test 4")
            val blockPos = interactionManager.getCurrentBreakingPos()!!
            println("Test 5")

            try {
                println("Test 6")
                interactionManager.sendPlayerAction2(action, blockPos, Direction.DOWN)
                println("Test 7")
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun onEnable() {
        Logger.info("Enabling fastbreak...")

        Client.getPlayer()?.sendMessage(Text.of("Enabling fastbreak!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling fastbreak...")

        Client.getPlayer()?.sendMessage(Text.of("Disabling fastbreak!"), false)
    }
}