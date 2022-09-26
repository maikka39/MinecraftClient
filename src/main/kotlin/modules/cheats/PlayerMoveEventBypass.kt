package modules.cheats

import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.PositionDirection

object PlayerMoveEventBypass : PaperPlayerMoveEventBypass("PlayerMoveEventBypass"), Keybinded {
    override val name = TranslatableText("cheat.modid.playermoveeventbypass.name")
    override val description = TranslatableText("cheat.modid.playermoveeventbypass.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.playermoveeventbypass", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
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
        for (i in 1..count) {
            val newPos = previousPosDir.pos.add(increment.multiply(i.toDouble()))

            sendPos(
                player,
                newPos,
                0f,
                0f,
            )
            resetPosition(player)
        }

        sendPos(player, packetPos.pos, 0f, 0f)
        resetPosition(player)
        return true
    }
}