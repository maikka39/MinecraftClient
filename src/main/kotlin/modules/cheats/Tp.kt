package modules.cheats

import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingDoubleSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object Tp : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.tp.name")
    override val description = Text.translatable("cheat.modid.tp.description")

    private val deltaX = Option<Double>(
            "options.modid.tp.deltaX.name",
        TooltipFactory.fromKey("options.modid.tp.deltaX.description"),
        ValueTextFactory.roundedDouble(1),
        ValidatingDoubleSliderCallbacks(-10.0, 10.0, 0.5),
        0.0,
    )

    private val deltaY = Option<Double>(
            "options.modid.tp.deltaY.name",
        TooltipFactory.fromKey("options.modid.tp.deltaY.description"),
        ValueTextFactory.roundedDouble(1),
        ValidatingDoubleSliderCallbacks(-10.0, 10.0, 0.5),
        0.0,
    )

    private val deltaZ = Option<Double>(
            "options.modid.tp.deltaZ.name",
        TooltipFactory.fromKey("options.modid.tp.deltaZ.description"),
        ValueTextFactory.roundedDouble(1),
        ValidatingDoubleSliderCallbacks(-10.0, 10.0, 0.5),
        0.0,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.tp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        if (!enabled) return

        val player = Client.player!!
        player.sendMessage(Text.of("TPing!"), false)
        player.setPosition(player.x + deltaX.value, player.y + deltaY.value, player.z + deltaZ.value)
    }
}