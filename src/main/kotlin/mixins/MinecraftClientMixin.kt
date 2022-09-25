package mixins

import event.EventManager
import events.client.RenderEvent
import events.world.TickEvent
import events.world.WorldEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.WindowEventHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.thread.ReentrantThreadExecutor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin(string: String?) : ReentrantThreadExecutor<Runnable>(string), WindowEventHandler {
    @Inject(at = [At("HEAD")], method = ["tick"])
    private fun onPreTick(info: CallbackInfo) {
        EventManager.notify(TickEvent.Pre())
    }

    @Inject(at = [At("TAIL")], method = ["tick"])
    private fun onTick(info: CallbackInfo) {
        EventManager.notify(TickEvent.Post())

    }

    @Inject(at = [At("HEAD")], method = ["render"])
    private fun onRender(tick: Boolean, info: CallbackInfo) {
        EventManager.notify(RenderEvent())
    }

    @Inject(at = [At("HEAD")], method = ["joinWorld"])
    private fun onJoinWorld(world: ClientWorld, info: CallbackInfo) {
        EventManager.notify(WorldEvent.Join(world))
    }
}