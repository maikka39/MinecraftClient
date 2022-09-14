package mixins

import event.EventManager
import events.packets.PacketEvent
import events.world.ConnectionEvent
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import modules.cheats.WorldGuardBypass
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.Packet
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.net.InetSocketAddress


@Mixin(ClientConnection::class)
class ClientConnectionMixin {
    @Inject(at = [At("HEAD")], method = ["disconnect"])
    private fun onDisconnect(disconnectReason: Text, ci: CallbackInfo) {
        EventManager.notify(ConnectionEvent.Disconnect(disconnectReason))
    }

    @Inject(at = [At("HEAD")], method = ["send(Lnet/minecraft/network/Packet;)V"], cancellable = true)
    private fun onSendPacketHead(packet: Packet<*>, info: CallbackInfo) {
        val event = EventManager.notify(PacketEvent.Send(packet))
        if (event.cancelled) info.cancel()
    }

    @Inject(at = [At("HEAD")], method = ["sendInternal"], cancellable = true)
    private fun sendInternal(
        packet: Packet<*>,
        callback: GenericFutureListener<out Future<in Void?>?>,
        packetState: NetworkState,
        currentState: NetworkState,
        info: CallbackInfo
    ) {
        if (WorldGuardBypass.enabled) {
            if (packet !is PlayerMoveC2SPacket) {
//                println(packet)
            }
        }
    }

    private companion object {
        @JvmStatic
        @Inject(at = [At("HEAD")], method = ["handlePacket"], cancellable = true)
        private fun <T : PacketListener?> onHandlePacket(packet: Packet<T>, listener: PacketListener, info: CallbackInfo) {
            val event = EventManager.notify(PacketEvent.Receive(packet))
            if (event.cancelled) info.cancel()
        }

        @JvmStatic
        @Inject(at = [At("HEAD")], method = ["connect"])
        private fun onConnect(
            address: InetSocketAddress,
            useEpoll: Boolean,
            info: CallbackInfoReturnable<ClientConnection>
        ) {
            EventManager.notify(ConnectionEvent.Connect(address))
        }
    }
}