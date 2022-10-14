package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.math.Direction
import org.lwjgl.glfw.GLFW
import utils.Global.Client

@ClientModule
object FastBreak : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.fastbreak.name")
    override val description = Text.translatable("cheat.modid.fastbreak.description")

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

            Client.networkHandler!!.sendPacket(
                PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                    interactionManager.currentBreakingPos!!,
                    Direction.DOWN
                )
            )
        }
    }
}