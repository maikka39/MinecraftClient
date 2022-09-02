package events.world

import event.Event
import net.minecraft.client.sound.SoundInstance

data class SoundEvent(val soundInstance: SoundInstance) : Event