package utils

import mixinterfaces.IPlayerMoveC2SPacket
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

object NetworkHandler {
    fun sendPacket(networkHandler: ClientPlayNetworkHandler, packet: PlayerMoveC2SPacket) {
        packet as IPlayerMoveC2SPacket
        packet.isMine = true
        networkHandler.sendPacket(packet)
    }
}