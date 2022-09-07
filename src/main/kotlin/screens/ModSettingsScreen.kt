package screens

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText

class ModSettingsScreen(parent: Screen?, gameOptions: GameOptions) :
    GameOptionsScreen(parent, gameOptions, TranslatableText("menu.modid.mod_settings")) {

    private lateinit var settingsListWidget: ModSettingsListWidget

    override fun init() {
        settingsListWidget = ModSettingsListWidget(this)
        addSelectableChild(this.settingsListWidget)

        addDrawableChild(ButtonWidget(
            width / 2 - 100, height - 27, 200, 20, ScreenTexts.DONE
        ) { client!!.setScreen(parent) })
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        this.settingsListWidget.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, title, width / 2, 8, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
    }
}