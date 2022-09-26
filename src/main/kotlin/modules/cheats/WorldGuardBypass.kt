package modules.cheats

import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.PositionDirection

object WorldGuardBypass : PaperPlayerMoveEventBypass("WorldGuardBypass"), Keybinded {
    override val name = TranslatableText("cheat.modid.worldguardbypass.name")
    override val description = TranslatableText("cheat.modid.worldguardbypass.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.worldguardbypass", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
        NoRotate.enabled = enabled
    }

    override fun onPacket(
        player: ClientPlayerEntity,
        packetPos: PositionDirection,
        previousPosDir: PositionDirection,
        posDiff: Vec3d,
        increment: Vec3d,
        count: Int
    ): Boolean {
        if (count < 1 || previousPosDir.pos.squaredDistanceTo(packetPos.pos) > 3 || previousPosDir.blockPos == packetPos.blockPos)
            return false

        val lastPosOnCurrentBlock = (0..count).findLast { i ->
            BlockPos(previousPosDir.pos.add(increment.multiply(i.toDouble()))) == previousPosDir.blockPos
        }!!

        sendPos(
            player,
            previousPosDir.pos.add(increment.multiply(lastPosOnCurrentBlock.toDouble())),
            previousPosDir.yaw,
            previousPosDir.pitch,
        )
        resetPosition(player)

        for (i in (lastPosOnCurrentBlock + 1)..count) {
            val newPos = previousPosDir.pos.add(increment.multiply(i.toDouble()))

            sendPos(
                player,
                newPos,
                previousPosDir.yaw,
                previousPosDir.pitch,
            )
            resetPosition(player)

            if (BlockPos(newPos) == packetPos.blockPos) break
        }

        sendPos(player, packetPos.pos, packetPos.yaw, packetPos.pitch)
        return true
    }
}