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
        WorldGuardBypass,
        LOAntiKick,
        LOWhitelistJoiner,
        NoFall,
        NoRotate,
        PlayerMoveEventBypass,
        Reach,
        ServerCrasher,
        Speed,
        SpeedMine,
        Spider,
        Tp,
        CheatMenuOpener,
    )

    val Cheats: List<Cheat> = ClientModules.filterIsInstance<Cheat>()
}
