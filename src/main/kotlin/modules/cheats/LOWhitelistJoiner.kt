package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket
import net.minecraft.text.TranslatableText
import utils.LastServerRememberer
import java.net.HttpURLConnection
import java.net.URL

object LOWhitelistJoiner : Cheat("LOWhitelistJoiner") {
    override var enabled = true
    override val name = TranslatableText("cheat.modid.lowhitelistjoiner.name")
    override val description = TranslatableText("cheat.modid.lowhitelistjoiner.description")

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is LoginHelloC2SPacket) return
        if (LastServerRememberer.lastServer!!.name != "LiveOverflow") return

        val loWhitelistKey = System.getenv("LOWhitelistKey")

        val url = URL("https://proxy-api.n00bbot.pet/kickone")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("accept", "application/json")
        connection.setRequestProperty("authorization", loWhitelistKey)
        connection.requestMethod = "GET"
        val response = connection.responseMessage
    }
}