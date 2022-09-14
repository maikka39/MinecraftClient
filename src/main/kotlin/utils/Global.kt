package utils

import modules.ClientModule
import modules.cheats.*
import modules.menuopeners.CheatMenuOpener
import net.minecraft.client.MinecraftClient

object Global {
    val Client: MinecraftClient = MinecraftClient.getInstance()

    val ClientModules: List<ClientModule> = listOf(
        AfkEndermanFarmer,
        AutoFish,
        AutoHighwayBuilder,
        Blink,
        FastBreak,
        Flight,
        FullBright,
        WorldGuardBypass,
        LOAntiKick,
        NoFall,
        NoRotate,
        Reach,
        Speed,
        SpeedMine,
        Spider,
        Tp,
        CheatMenuOpener,
    )

    val Cheats: List<Cheat> = ClientModules.filterIsInstance<Cheat>()
}
