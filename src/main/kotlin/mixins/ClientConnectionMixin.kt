package mixins

import event.EventManager
import events.packets.PacketEvent
import events.world.ConnectionEvent
import net.minecraft.network.ClientConnection
import net.minecraft.network.Packet
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
        EventManager.notify(PacketEvent.Send(packet))
    }

    private companion object {
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