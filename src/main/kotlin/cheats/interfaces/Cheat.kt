package cheats.interfaces

import net.minecraft.text.TranslatableText

interface Cheat {
    var enabled: Boolean

    val name: TranslatableText
    val description: TranslatableText


    fun initialize(): Unit
}