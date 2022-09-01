package modules

import event.EventHandler
import events.world.TickEvent
import net.minecraft.client.option.KeyBinding

interface Keybinded {
    val keyBinding: KeyBinding

    fun onKeybindingPressed()

    @EventHandler(TickEvent.Pre::class)
    fun Keybinded.checkIfKeybindingWasPressed() {
        while (keyBinding.wasPressed())
            onKeybindingPressed()
    }
}