package utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import utils.Global.Client

object LastServerRememberer {
    var lastServer: ServerInfo? = null

    fun reconnect(prevScreen: Screen?) {
        if (lastServer == null) return
        ConnectScreen.connect(
            prevScreen, Client as MinecraftClient,
            ServerAddress.parse(lastServer!!.address), lastServer
        )
    }
}