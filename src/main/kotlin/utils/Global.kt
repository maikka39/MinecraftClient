package utils

import modules.ClientModule
import modules.cheats.*
import modules.menuopeners.CheatMenuOpener
import net.minecraft.client.MinecraftClient

object Global {
    val Client: MinecraftClient = MinecraftClient.getInstance()

    val ClientModules: List<ClientModule> = listOf(
        AfkEndermanFarmer(),
        AutoFish(),
        FastBreak(),
        Flight(),
        FullBright(),
        LOAntiKick(),
        NoFall(),
        Reach(),
        Speed(),
        SpeedMine(),
        Spider(),
        CheatMenuOpener(),
    )

    val Cheats: List<Cheat> = ClientModules.filterIsInstance<Cheat>()
}
