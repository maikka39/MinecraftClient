package modules.cheats

import modules.ClientModule
import net.minecraft.client.option.Option
import net.minecraft.text.TranslatableText

interface Cheat : ClientModule {
    var enabled: Boolean

    val options: List<Option>

    val name: TranslatableText
    val description: TranslatableText
}