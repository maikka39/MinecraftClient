package modules.cheats

import net.minecraft.text.TranslatableText
import modules.ClientModule

interface Cheat : ClientModule {
    var enabled: Boolean

    val name: TranslatableText
    val description: TranslatableText
}