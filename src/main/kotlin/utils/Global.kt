package utils

import mixinterfaces.IMinecraftClient
import modules.cheats.*
import net.minecraft.client.MinecraftClient
import modules.ClientModule
import modules.menuopeners.CheatMenuOpener

object Global {
    val Client: IMinecraftClient = MinecraftClient.getInstance() as IMinecraftClient

    val ClientModules: List<ClientModule> = listOf(Flight(), FullBright(), Speed(), FastBreak(), CheatMenuOpener())
    val Cheats: List<Cheat> = ClientModules.filterIsInstance<Cheat>()
}
