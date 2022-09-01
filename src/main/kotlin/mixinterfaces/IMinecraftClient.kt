package mixinterfaces

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.GameOptions
import net.minecraft.client.world.ClientWorld

interface IMinecraftClient {
    fun getCurrentScreen(): Screen?
    fun setCurrentScreen(screen: Screen?)

    fun getOptions(): GameOptions

    fun getPlayer(): ClientPlayerEntity?
    fun getWorld(): ClientWorld?
    fun getInteractionManager(): IClientPlayerInteractionManager?
    fun getNetworkHandler(): ClientPlayNetworkHandler?

    fun setScreen(screen: Screen?)
}