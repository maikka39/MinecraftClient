package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingIntSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object AutoHighwayBuilder : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.autohighwaybuilder.name")
    override val description = Text.translatable("cheat.modid.autohighwaybuilder.description")

    private val tunnelWidth = Option<Int>(
        "options.modid.autohighwaybuilder.tunnelWidth.name",
        TooltipFactory.fromKey("options.modid.autohighwaybuilder.tunnelWidth.description"),
        ValueTextFactory.simpleInt,
        ValidatingIntSliderCallbacks(1, 5),
        2,
    )

    private val tunnelHeight = Option<Int>(
        "options.modid.autohighwaybuilder.tunnelHeight.name",
        TooltipFactory.fromKey("options.modid.autohighwaybuilder.tunnelHeight.description"),
        ValueTextFactory.simpleInt,
        ValidatingIntSliderCallbacks(1, 5),
        3,
    )

    private val amountOfBlocksToPlacePerTick = Option<Int>(
        "options.modid.autohighwaybuilder.amountOfBlocksToPlacePerTick.name",
        TooltipFactory.fromKey("options.modid.autohighwaybuilder.amountOfBlocksToPlacePerTick.description"),
        ValueTextFactory.simpleInt,
        ValidatingIntSliderCallbacks(1, 5),
        1,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.autohighwaybuilder", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private val blockPlaceQueue: MutableList<BlockPos> = mutableListOf()
    private val blockBreakQueue: MutableList<BlockPos> = mutableListOf()

    @EventHandler(TickEvent.Pre::class)
    fun onTick() {
        if (!enabled) return

        val player = Client.player ?: return

        val right = player.movementDirection.rotateYClockwise()
        val left = player.movementDirection.rotateYCounterclockwise()

        val basePos = player.blockPos.offset(player.movementDirection, 1)

        for (n in 0 until tunnelHeight.value) {
            blockPlaceQueue.add(basePos.offset(left, 1).up(n))
            blockPlaceQueue.add(basePos.offset(right, tunnelWidth.value).up(n))
        }

        for (n in 0 until tunnelWidth.value) {
            blockPlaceQueue.add(basePos.up(tunnelHeight.value).offset(right, n))
            blockPlaceQueue.add(basePos.down(1).offset(right, n))
        }

        for (n in 0 until tunnelHeight.value) {
            for (m in 0 until tunnelWidth.value) {
                val pos = basePos.offset(right, m).up(n)
                if (!blockBreakQueue.contains(pos))
                    blockBreakQueue.add(pos)
            }
        }

        var placeCounter = 0
        val blocksToRemoveFromPlaceQueue = mutableListOf<BlockPos>()
        for (blockPos in blockPlaceQueue) {
            val blockChanged = placeBlockIfAir(blockPos)

            blocksToRemoveFromPlaceQueue.add(blockPos)

            if (blockChanged)
                placeCounter++

            if (placeCounter >= amountOfBlocksToPlacePerTick.value)
                break
        }
        blocksToRemoveFromPlaceQueue.forEach(blockPlaceQueue::remove)

        var breakCounter = 0
        val blocksToRemoveFromBreakQueue = mutableListOf<BlockPos>()
        for (blockPos in blockBreakQueue) {
            val blockChanged = breakBlockIfNotAir(blockPos)

            blocksToRemoveFromBreakQueue.add(blockPos)

            if (blockChanged)
                breakCounter++

            if (breakCounter >= 1)
                break
        }
        blocksToRemoveFromBreakQueue.forEach(blockBreakQueue::remove)
    }

    private fun placeBlockIfAir(pos: BlockPos): Boolean {
        val blockState = Client.world!!.getBlockState(pos)
        return if (blockState.isAir || blockState.fluidState.isStill) {
            Client.interactionManager!!.interactBlock(
                Client.player,
                Hand.OFF_HAND,
                BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false)
            )
            true
        } else
            false
    }

    private fun breakBlockIfNotAir(pos: BlockPos): Boolean {
        val blockState = Client.world!!.getBlockState(pos)
        return if (!blockState.isAir) {
            Client.networkHandler!!.sendPacket(
                PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                    pos,
                    Direction.DOWN
                )
            )
            Client.networkHandler!!.sendPacket(
                PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                    pos,
                    Direction.DOWN
                )
            )
            Client.networkHandler!!.sendPacket(HandSwingC2SPacket(Hand.MAIN_HAND))
            true
        } else
            false
    }
}