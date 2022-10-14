package events.packets

import event.CancellableEvent
import event.Event
import net.minecraft.network.Packet

object PacketEvent {
    data class Send(val packet: Packet<*>) : CancellableEvent()
    data class Sent(val packet: Packet<*>) : Event
    data class Receive(val packet: Packet<*>) : CancellableEvent()
}