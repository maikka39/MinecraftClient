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
        Criticals,
        FastBreak,
        Flight,
        FullBright,
        LOAntiKick,
        NoFall,
        NoRotate,
        PlayerMoveEventBypass,
        Reach,
        ServerCrasher,
        Speed,
        SpeedMine,
        Spider,
        Tp,
        WorldGuardBypass,
        CheatMenuOpener,
    )

    val Cheats: List<Cheat> = ClientModules.filterIsInstance<Cheat>()
}
