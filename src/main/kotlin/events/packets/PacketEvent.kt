package events.packets

import event.Event
import net.minecraft.network.Packet

object PacketEvent {
    data class Send(val packet: Packet<*>) : Event
}