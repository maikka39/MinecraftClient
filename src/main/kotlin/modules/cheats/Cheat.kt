package modules.cheats

import Logger
import modules.ClientModule
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import utils.Global.Client
import utils.options.BaseOption
import utils.options.Option
import utils.options.OptionCallbacks.Companion.BooleanCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible


abstract class Cheat {
    private val enabledOption = Option<Boolean>(
        "other.modid.enabled",
        TooltipFactory.emptyTooltip,
        ValueTextFactory.onOff,
        BooleanCallbacks(),
        false,
    ) { value -> if (value) onEnable() else onDisable() }

    var enabled: Boolean
        get() = enabledOption.value
        set(value) {
            enabledOption.value = value
        }

    abstract val name: Text
    abstract val description: Text

    val options: List<BaseOption>
        get() {
            val privateParentProperties = this::class.allSuperclasses
                .flatMap { it.memberProperties }
                .filter { it.visibility == KVisibility.PRIVATE }

            return (privateParentProperties + this::class.memberProperties)
                .filter { it.returnType.isSubtypeOf(BaseOption::class.starProjectedType) }
                .map {
                    it.isAccessible = true

                    try {
                        it.call()
                    } catch (e: IllegalArgumentException) {
                        it.call(this)
                    } catch (e: IllegalArgumentException) {
                        throw e
                    }
                }
                .map { it as BaseOption }
        }

    protected open fun onEnable() {
        Logger.info("Enabling ${this::class.simpleName}...")
        name.visit { name ->
            Client.player?.sendMessage(Text.translatable("other.modid.enabledcheat", name), false)
            StringVisitable.TERMINATE_VISIT
        }
    }

    protected open fun onDisable() {
        Logger.info("Disabling ${this::class.simpleName}...")
        name.visit { name ->
            Client.player?.sendMessage(Text.translatable("other.modid.disabledcheat", name), false)
            StringVisitable.TERMINATE_VISIT
        }
    }
}
