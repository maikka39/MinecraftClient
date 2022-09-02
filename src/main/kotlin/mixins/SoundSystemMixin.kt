package mixins

import event.EventManager
import events.world.SoundEvent
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundSystem
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(SoundSystem::class)
class SoundSystemMixin {
    @Inject(method = ["play(Lnet/minecraft/client/sound/SoundInstance;)V"], at = [At("HEAD")], cancellable = true)
    private fun onPlay(soundInstance: SoundInstance, info: CallbackInfo) {
        EventManager.notify(SoundEvent(soundInstance))
    }
}