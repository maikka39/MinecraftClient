package screens

import com.google.common.collect.ImmutableList
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.option.CyclingOption
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.Option
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import utils.Global.Cheats
import utils.Global.Client

class ModSettingsListWidget(parent: Screen) : ElementListWidget<ModSettingsListWidget.Entry>(
    Client, parent.width + 80, parent.height, 20, parent.height - 32, 25
) {
    fun getHoveredWidget(mouseX: Double, mouseY: Double): ClickableWidget? {
        return children().flatMap { it.children() }.find { it.isMouseOver(mouseX, mouseY) } as ClickableWidget?
    }

    init {
        Cheats.forEach { cheat ->
            addEntry(CategoryEntry(cheat.name, cheat.description))

            val enabledOption = CyclingOption.create("option.modid.enabled",
                { cheat.enabled },
                { _, _, value: Boolean -> cheat.enabled = value })

            (listOf(enabledOption) + cheat.options).chunked(2).forEach {
                addEntry(OptionEntry(it[0], it.getOrNull(1)))
            }
        }
    }

    abstract class Entry : ElementListWidget.Entry<Entry?>()

    inner class CategoryEntry(val text: Text, val description: Text) : Entry() {
        private val textWidth: Int = client.textRenderer.getWidth(text as StringVisitable)
        private val descriptionWidth: Int = client.textRenderer.getWidth(description as StringVisitable)

        override fun render(
            matrices: MatrixStack,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val renderX = (client.currentScreen!!.width / 2).toFloat()
            val renderY = (y + entryHeight - 9 - 1).toFloat()
            if (!hovered)
                client.textRenderer.draw(matrices, text, renderX - textWidth / 2, renderY, 0xFFFFFF)
            else
                client.textRenderer.draw(matrices, description, renderX - descriptionWidth / 2, renderY, 0xCCCCCC)
        }

        override fun changeFocus(lookForwards: Boolean): Boolean {
            return false
        }

        override fun children(): List<Element> {
            return emptyList()
        }

        override fun selectableChildren(): List<Selectable?> {
            return ImmutableList.of(object : Selectable {
                override fun getType(): Selectable.SelectionType {
                    return Selectable.SelectionType.HOVERED
                }

                override fun appendNarrations(builder: NarrationMessageBuilder) {
                    builder.put(NarrationPart.TITLE, text)
                }
            })
        }
    }

    inner class OptionEntry(firstOption: Option, secondOption: Option?) : Entry() {
        private val buttons: Pair<ClickableWidget, ClickableWidget?>

        init {
            val button1 = firstOption.createButton(Client.options, client.currentScreen!!.width / 2 - 155, 0, 150)
            val button2 = secondOption?.createButton(Client.options, client.currentScreen!!.width / 2 + 5, 0, 150)
            buttons = Pair(button1, button2)
        }

        override fun render(
            matrices: MatrixStack,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val (button1, button2) = buttons
            button1.y = y
            button1.render(matrices, mouseX, mouseY, tickDelta)

            button2?.let {
                it.y = y
                it.render(matrices, mouseX, mouseY, tickDelta)

            }
        }

        override fun children(): List<Element?> {
            return buttons.toList().filterNotNull()
        }

        override fun selectableChildren(): List<Selectable?> {
            return buttons.toList().filterNotNull()
        }
    }

    companion object {
        val getIntLabel = { gameOptions: GameOptions?, option: DoubleOption ->
            val ratio = option.getRatio(option.get(gameOptions))
            option.getGenericLabel(option.getValue(ratio).toInt())
        }

        val getDoubleLabel = { gameOptions: GameOptions?, option: DoubleOption ->
            val ratio = option.getRatio(option.get(gameOptions))
            option.getGenericLabel(LiteralText(String.format("%.2f", option.getValue(ratio))))
        }

        val getTooltipFromKey: (String) -> (MinecraftClient) -> List<OrderedText> = { key ->
            { client ->
                client.textRenderer.wrapLines(
                    TranslatableText(key), 200
                )
            }
        }
    }
}