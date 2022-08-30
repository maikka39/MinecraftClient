package mixinterfaces

import net.minecraft.client.network.ServerInfo

interface IMultiplayerScreen {
    fun connectToServer(server: ServerInfo)
}