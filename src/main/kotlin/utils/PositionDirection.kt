package utils

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

data class PositionDirection(val pos: Vec3d, val yaw: Float, val pitch: Float) {
    val blockPos = BlockPos(pos)
}
