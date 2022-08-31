package mixinterfaces

import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld

interface IMinecraftClient {
    fun getPlayer(): ClientPlayerEntity?
    fun getWorld(): ClientWorld?
    fun getInteractionManager(): IClientPlayerInteractionManager?
    fun getNetworkHandler(): ClientPlayNetworkHandler?
}