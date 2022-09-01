package mixins

import event.EventManager
import events.world.TickEvent
import mixinterfaces.IClientPlayerInteractionManager
import mixinterfaces.IMinecraftClient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.WindowEventHandler
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.option.GameOptions
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
    private var interactionManager: ClientPlayerInteractionManager? = null

    @Shadow
    private var player: ClientPlayerEntity? = null

    @Shadow
    private var world: ClientWorld? = null

    @Shadow
    private lateinit var options: GameOptions

    @Shadow
    private var currentScreen: Screen? = null

    override fun getOptions(): GameOptions {
        return options
    }

    override fun getCurrentScreen(): Screen? {
        return currentScreen
    }
    override fun setCurrentScreen(screen: Screen?) {
        currentScreen = screen
    }

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

    @Shadow
    override fun setScreen(screen: Screen?) {
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