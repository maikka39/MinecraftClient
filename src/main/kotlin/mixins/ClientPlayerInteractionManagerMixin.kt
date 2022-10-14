package mixins

import modules.cheats.Reach
import net.minecraft.client.network.ClientPlayerInteractionManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(ClientPlayerInteractionManager::class)
abstract class ClientPlayerInteractionManagerMixin {
    @Inject(at = [At("RETURN")], method = ["getReachDistance"], cancellable = true)
    private fun getReachDistance(info: CallbackInfoReturnable<Float>) {
        if (!Reach.enabled) return

        info.returnValue = Reach.reach.value.toFloat()
    }
}