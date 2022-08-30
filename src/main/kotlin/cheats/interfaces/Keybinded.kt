package cheats.interfaces

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.option.KeyBinding

interface Keybinded {
    var enabled: Boolean
    val keyBinding: KeyBinding

    fun initialize() {
        ClientTickEvents.END_CLIENT_TICK.register {
            while (keyBinding.wasPressed()) {
                enabled = !enabled
            }
        }
    }
}