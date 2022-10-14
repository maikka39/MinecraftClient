package utils.options

import com.google.common.collect.ImmutableList
import com.mojang.serialization.Codec
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.option.SimpleOption
import net.minecraft.client.option.SimpleOption.Callbacks
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import utils.Global.Client
import java.util.*
import java.util.function.Consumer


private typealias TooltipFactoryAnyType = (Any) -> List<OrderedText>

fun interface TooltipFactory<in T> {
    fun apply(value: T): List<OrderedText>

    companion object {
        val emptyTooltip: TooltipFactoryAnyType = { ImmutableList.of() }
        fun fromKey(key: String): TooltipFactoryAnyType {
            return { Client.textRenderer.wrapLines(Text.translatable(key), 200) }
        }
    }
}


fun interface ValueTextFactory<in T> {
    fun apply(name: Text, value: T): Text

    companion object {
        fun roundedDouble(decimalPlaces: Int): ValueTextFactory<Double> = ValueTextFactory { name, value ->
            name.copy().append(String.format(Locale.ROOT, ": %.${decimalPlaces}f", value))
        }

        val simpleInt: ValueTextFactory<Int> = ValueTextFactory { name, value -> name.copy().append(": $value") }

        val onOff: ValueTextFactory<Boolean> = ValueTextFactory { _, value ->
            if (value) Text.translatable("other.modid.on")
            else Text.translatable("other.modid.off")
        }
    }
}

sealed interface OptionCallbacks<T> {
    val callback: Callbacks<T>
    val codec: Codec<T>

    companion object {
        class ValidatingIntSliderCallbacks(min: Int, max: Int) : OptionCallbacks<Int> {
            override val callback = SimpleOption.ValidatingIntSliderCallbacks(min, max)
            override val codec = Codec.intRange(min, max)
        }

        class ValidatingDoubleSliderCallbacks(min: Double, max: Double, step: Double) : OptionCallbacks<Double> {
            private val multiplier = 1 / step
            override val callback =
                SimpleOption.ValidatingIntSliderCallbacks((min * multiplier).toInt(), (max * multiplier).toInt())
                    .withModifier(
                        { value -> value.toDouble() / multiplier },
                        { value -> (value * multiplier).toInt() }
                    )
            override val codec = Codec.doubleRange(min, max)
        }

        class BooleanCallbacks : OptionCallbacks<Boolean> {
            override val callback = SimpleOption.BOOLEAN
            override val codec = SimpleOption.BOOLEAN.codec
        }
    }
}

sealed interface BaseOption {
    fun createButton(x: Int, y: Int, width: Int): ClickableWidget
}

class Option<T>(
    key: String,
    tooltip: TooltipFactory<T>,
    valueText: ValueTextFactory<T>,
    callback: OptionCallbacks<T>,
    val defaultValue: T,
    onChange: Consumer<T> = Consumer {}
) : BaseOption {
    private val mcOption: SimpleOption<T> = SimpleOption(
        key,
        { _ -> SimpleOption.TooltipFactory { tooltip.apply(it) } },
        { optionText, value -> valueText.apply(optionText, value) },
        callback.callback,
        callback.codec,
        defaultValue,
        onChange
    )

    var value: T
        get() {
            return mcOption.value
        }
        set(value) {
            mcOption.value = value
        }

    override fun createButton(x: Int, y: Int, width: Int): ClickableWidget {
        return mcOption.createButton(Client.options, x, y, width)
    }
}