package modules.cheats

import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingDoubleSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object Reach : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.reach.name")
    override val description = Text.translatable("cheat.modid.reach.description")

    val reach = Option<Double>(
        "options.modid.autofish.reelInDelay.name",
        TooltipFactory.fromKey("options.modid.autofish.reelInDelay.description"),
        ValueTextFactory.roundedDouble(2),
        ValidatingDoubleSliderCallbacks(3.0, 6.0, 0.5),
        5.0,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.reach", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }
}