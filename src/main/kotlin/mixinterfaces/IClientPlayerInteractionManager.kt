package mixinterfaces

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

interface IClientPlayerInteractionManager {
    fun getCurrentBreakingProgress(): Float
    fun isBreakingBlock(): Boolean
    fun breakBlock(pos: BlockPos?): Boolean
    fun getCurrentBreakingPos(): BlockPos?

    fun sendPlayerAction2(action: PlayerActionC2SPacket.Action, pos: BlockPos, direction: Direction)
}