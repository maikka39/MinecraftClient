package mixins

import event.EventManager
import events.world.TickEvent
import mixinterfaces.IClientPlayerInteractionManager
import mixinterfaces.IMinecraftClient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.WindowEventHandler
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.thread.ReentrantThreadExecutor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin(string: String?) : ReentrantThreadExecutor<Runnable>(string), WindowEventHandler,
    IMinecraftClient {

    @Shadow
    private val interactionManager: ClientPlayerInteractionManager? = null

    @Shadow
    private val player: ClientPlayerEntity? = null

    @Shadow
    private val world: ClientWorld? = null

    override fun getNetworkHandler(): ClientPlayNetworkHandler? {
        return player?.networkHandler
    }

    override fun getInteractionManager(): IClientPlayerInteractionManager? {
        return interactionManager as IClientPlayerInteractionManager?
    }

    override fun getPlayer(): ClientPlayerEntity? {
        return player
    }

    override fun getWorld(): ClientWorld? {
        return world
    }

    @Inject(at = [At("HEAD")], method = ["tick"])
    private fun onPreTick(info: CallbackInfo) {
        EventManager.notify(TickEvent.Pre())
    }

    @Inject(at = [At("TAIL")], method = ["tick"])
    private fun onTick(info: CallbackInfo) {
        EventManager.notify(TickEvent.Post())

    }
}