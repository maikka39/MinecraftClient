package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingDoubleSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object Spider : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.spider.name")
    override val description = Text.translatable("cheat.modid.spider.description")

    private val climbingSpeed = Option<Double>(
            "options.modid.spider.climbingSpeed.name",
        TooltipFactory.fromKey("options.modid.spider.climbingSpeed.description"),
        ValueTextFactory.roundedDouble(2),
        ValidatingDoubleSliderCallbacks(0.05, 1.0, 0.05),
        0.2,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.spider", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(TickEvent.Post::class)
    private fun afterTick() {
        if (!enabled) return

        Client.player?.let {
            if (!it.horizontalCollision) return

            if (it.velocity.y >= climbingSpeed.value) return

            it.velocity = Vec3d(it.velocity.x, climbingSpeed.value, it.velocity.z)
        }
    }
}