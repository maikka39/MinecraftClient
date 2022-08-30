package utils

import cheats.*
import cheats.interfaces.Cheat
import mixinterfaces.IMinecraftClient
import net.minecraft.client.MinecraftClient

object Global {
    val Client: IMinecraftClient = MinecraftClient.getInstance() as IMinecraftClient

    val Cheats: List<Cheat> = listOf(Flight(), FullBright(), Speed(), FastBreak())
}