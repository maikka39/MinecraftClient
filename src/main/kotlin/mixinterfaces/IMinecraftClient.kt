package mixinterfaces

import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity

interface IMinecraftClient {
    fun getPlayer(): ClientPlayerEntity?
    fun getInteractionManager(): IClientPlayerInteractionManager?
    fun getNetworkHandler(): ClientPlayNetworkHandler?
}