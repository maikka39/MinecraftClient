package events.world

import event.Event
import net.minecraft.text.Text
import java.net.InetSocketAddress

object ConnectionEvent {
    data class Connect(val address: InetSocketAddress) : Event
    data class Disconnect(val disconnectReason: Text) : Event
}