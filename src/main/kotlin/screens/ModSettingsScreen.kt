package screens

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.TooltipSupplier
import net.minecraft.client.option.GameOptions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import utils.Global.Cheats
import java.util.function.Consumer

class ModSettingsScreen(parent: Screen?, options: GameOptions) :
    GameOptionsScreen(parent, options, TranslatableText("menu.modid.mod_settings")) {

    override fun init() {
        super.init()

        val buttonWidth = 98
        val buttonHeight = 20

        Cheats.forEachIndexed { index, cheat ->
            val x = this.width / 2 + (if (index % 2 == 0) -102 else 4)
            val y = this.height / 4 + 24 * (index / 2 + 1) + -16

            val onPress = { _: ButtonWidget ->
                cheat.enabled = !cheat.enabled
                MinecraftClient.getInstance().setScreen(ModSettingsScreen(parent, gameOptions))
            }

            val onTooltip = object : TooltipSupplier {
                override fun onTooltip(buttonWidget: ButtonWidget, matrixStack: MatrixStack, i: Int, j: Int) {
                    this@ModSettingsScreen.renderTooltip(matrixStack, cheat.description, i, j)
                }

                override fun supply(consumer: Consumer<Text>) {
                    consumer.accept(cheat.description)
                }
            }

            addDrawableChild(
                ButtonWidget(
                    x,
                    y,
                    buttonWidth,
                    buttonHeight,
                    TranslatableText(cheat.name.string + ": " + if (cheat.enabled) "ON" else "OFF"),
                    onPress,
                    onTooltip
                )
            )
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        drawCenteredText(matrices, textRenderer, title, width / 2, 40, 16777215)

        super.render(matrices, mouseX, mouseY, delta)
    }
}