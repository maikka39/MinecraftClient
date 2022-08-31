package cheats.interfaces

import event.EventHandler
import events.world.TickEvent
import net.minecraft.client.option.KeyBinding

interface Keybinded {
    var enabled: Boolean
    val keyBinding: KeyBinding

    @EventHandler(TickEvent.Pre::class)
    fun Keybinded.checkIfKeybindingWasPressed() {
        while (keyBinding.wasPressed()) {
            enabled = !enabled
        }
    }
}