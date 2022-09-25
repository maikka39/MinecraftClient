package events.world

import event.Event
import net.minecraft.client.world.ClientWorld

object WorldEvent {
    data class Join(val world: ClientWorld) : Event
}