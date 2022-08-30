package mixins

import mixinterfaces.IClientPlayerInteractionManager
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow

@Mixin(ClientPlayerInteractionManager::class)
abstract class ClientPlayerInteractionManagerMixin : IClientPlayerInteractionManager {
    @Shadow
    private var currentBreakingProgress: Float = 0f

    @Shadow
    private var breakingBlock: Boolean = false

    @Shadow
    private var currentBreakingPos: BlockPos? = null

    override fun getCurrentBreakingProgress(): Float {
        return currentBreakingProgress
    }

    override fun isBreakingBlock(): Boolean {
        return breakingBlock
    }

    override fun getCurrentBreakingPos(): BlockPos? {
        return currentBreakingPos
    }

    @Shadow
    abstract override fun breakBlock(pos: BlockPos?): Boolean

//    override fun sendPlayerActionC2SPacket(
//        action: PlayerActionC2SPacket.Action?,
//        blockPos: BlockPos?,
//        direction: Direction?
//    ) {
//        TODO("Not yet implemented")
//    }

    @Shadow
    private fun sendPlayerAction(action: PlayerActionC2SPacket.Action, pos: BlockPos, direction: Direction) {}

    override fun sendPlayerAction2(action: PlayerActionC2SPacket.Action, pos: BlockPos, direction: Direction) {
        sendPlayerAction(action, pos, direction)
    }
}