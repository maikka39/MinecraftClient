package screens

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.util.OrderableTooltip
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.OrderedText
import net.minecraft.text.Text

class ModSettingsScreen(parent: Screen?, gameOptions: GameOptions) :
    GameOptionsScreen(parent, gameOptions, Text.translatable("menu.modid.mod_settings")) {

    private lateinit var settingsListWidget: ModSettingsListWidget

    override fun init() {
        settingsListWidget = ModSettingsListWidget(this)
        addSelectableChild(settingsListWidget)

        addDrawableChild(ButtonWidget(
            width / 2 - 100, height - 27, 200, 20, ScreenTexts.DONE
        ) { client!!.setScreen(parent) })
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        settingsListWidget.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, title, width / 2, 8, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
        val list = getHoveredWidgetTooltip(mouseX, mouseY)
        renderOrderedTooltip(matrices, list, mouseX, mouseY)
    }

    private fun getHoveredWidgetTooltip(mouseX: Int, mouseY: Int): List<OrderedText> {
        val widget = settingsListWidget.getHoveredWidget(mouseX.toDouble(), mouseY.toDouble())
        return if (widget != null && widget is OrderableTooltip) widget.orderedTooltip
        else listOf()
    }
}