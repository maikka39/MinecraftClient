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
object SpeedMine : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.speedmine.name")
    override val description = Text.translatable("cheat.modid.speedmine.description")

    val speedModifier = Option<Double>(
        "options.modid.speedmine.speedModifier.name",
        TooltipFactory.fromKey("options.modid.speedmine.speedModifier.description"),
        ValueTextFactory.roundedDouble(2),
        ValidatingDoubleSliderCallbacks(0.6, 2.0, 0.05),
        1.6,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.speedmine", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }
}