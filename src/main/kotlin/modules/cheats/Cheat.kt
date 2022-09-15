package modules.cheats

import Logger
import modules.ClientModule
import net.minecraft.client.option.Option
import net.minecraft.text.TranslatableText
import utils.Global.Client

abstract class Cheat(private val id: String) : ClientModule {
    open var enabled: Boolean = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    abstract val name: TranslatableText
    abstract val description: TranslatableText

    open val options: List<Option> = listOf()

    protected open fun onEnable() {
        Logger.info("Enabling $id...")
        Client.player?.sendMessage(TranslatableText("other.modid.enabledcheat", id), false)
    }

    protected open fun onDisable() {
        Logger.info("Disabling $id...")
        Client.player?.sendMessage(TranslatableText("other.modid.disabledcheat", id), false)
    }
}
