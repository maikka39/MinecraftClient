package mixins

import mixinterfaces.IClientPlayerInteractionManager
import mixinterfaces.IMinecraftClient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.WindowEventHandler
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.Entity
import net.minecraft.util.thread.ReentrantThreadExecutor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin(string: String?) : ReentrantThreadExecutor<Runnable>(string), WindowEventHandler,
    IMinecraftClient {

    @Shadow
    private val interactionManager: ClientPlayerInteractionManager? = null

    @Shadow
    private val player: ClientPlayerEntity? = null

    override fun getNetworkHandler(): ClientPlayNetworkHandler? {
        return player?.networkHandler
    }

    override fun getInteractionManager(): IClientPlayerInteractionManager? {
        return interactionManager as IClientPlayerInteractionManager?
    }

    override fun getPlayer(): ClientPlayerEntity? {
        return player
    }
}