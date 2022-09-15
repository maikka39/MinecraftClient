package modules.cheats

import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.Global.Client

object Tp : Cheat("Tp"), Keybinded {
    override var enabled = true

    override val name = TranslatableText("cheat.modid.tp.name")
    override val description = TranslatableText("cheat.modid.tp.description")

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.tp.deltaX.name",
            -10.0,
            10.0,
            0.5f,
            { deltaX.toDouble() },
            { _, value: Double ->
                deltaX = value.toInt()
            },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.tp.deltaX.description"),
        ),
        DoubleOption(
            "options.modid.tp.deltaY.name",
            -10.0,
            10.0,
            0.5f,
            { deltaY.toDouble() },
            { _, value: Double ->
                deltaY = value.toInt()
            },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.tp.deltaY.description"),
        ),
        DoubleOption(
            "options.modid.tp.deltaZ.name",
            -10.0,
            10.0,
            0.5f,
            { deltaZ.toDouble() },
            { _, value: Double ->
                deltaZ = value.toInt()
            },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.tp.deltaZ.description"),
        ),
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.tp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.modid.cheat"
        )
    )!!

    private var deltaX: Int = 0
    private var deltaY: Int = 0
    private var deltaZ: Int = 0

    override fun onKeybindingPressed() {
        if (!enabled) return

        val player = Client.player!!
        player.sendMessage(Text.of("TPing!"), false)
        player.setPosition(player.x + deltaX, player.y + deltaY, player.z + deltaZ)
    }
}