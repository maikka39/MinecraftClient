package modules.cheats

import Logger
import event.EventHandler
import events.world.TickEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.DelayedAction
import utils.Global.Client

object AutoHighwayBuilder : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.autohighwaybuilder.tunnelWidth.name",
            1.0,
            5.0,
            1.0f,
            { tunnelWidth.toDouble() },
            { _, value: Double ->
                tunnelWidth = value.toInt()
            },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.autohighwaybuilder.tunnelWidth.description"),
        ),
        DoubleOption(
            "options.modid.autohighwaybuilder.tunnelHeight.name",
            1.0,
            5.0,
            1.0f,
            { tunnelHeight.toDouble() },
            { _, value: Double -> tunnelHeight = value.toInt() },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.autohighwaybuilder.tunnelHeight.description"),
        ),
        DoubleOption(
            "options.modid.autohighwaybuilder.amountOfBlocksToPlacePerTick.name",
            1.0,
            5.0,
            1.0f,
            { amountOfBlocksToPlacePerTick.toDouble() },
            { _, value: Double -> amountOfBlocksToPlacePerTick = value.toInt() },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.autohighwaybuilder.amountOfBlocksToPlacePerTick.description"),
        ),
        DoubleOption(
            "options.modid.autohighwaybuilder.amountOfBlocksToBreakPerTick.name",
            1.0,
            5.0,
            1.0f,
            { amountOfBlocksToBreakPerTick.toDouble() },
            { _, value: Double -> amountOfBlocksToBreakPerTick = value.toInt() },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.autohighwaybuilder.amountOfBlocksToBreakPerTick.description"),
        ),
    )

    override val name = TranslatableText("cheat.modid.autohighwaybuilder.name")
    override val description = TranslatableText("cheat.modid.autohighwaybuilder.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.autohighwaybuilder", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.modid.cheat"
        )
    )!!

    var tunnelWidth: Int = 2
    var tunnelHeight: Int = 3
    var amountOfBlocksToPlacePerTick: Int = 1
    var amountOfBlocksToBreakPerTick: Int = 1

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private val blockPlaceQueue: MutableList<BlockPos> = mutableListOf()
    private val blockBreakQueue: MutableList<BlockPos> = mutableListOf()

    @EventHandler(TickEvent.Pre::class)
    fun onTick() {
        if (!enabled) return

        Client.player?.let { player ->
            val right = player.movementDirection.rotateYClockwise()
            val left = player.movementDirection.rotateYCounterclockwise()

            val basePos = player.blockPos.offset(player.movementDirection, 1)

            for (n in 0 until tunnelHeight) {
                blockPlaceQueue.add(basePos.offset(left, 1).up(n))
                blockPlaceQueue.add(basePos.offset(right, tunnelWidth).up(n))
            }

            for (n in 0 until tunnelWidth) {
                blockPlaceQueue.add(basePos.up(tunnelHeight).offset(right, n))
                blockPlaceQueue.add(basePos.down(1).offset(right, n))
            }

            for (n in 0 until tunnelHeight) {
                for (m in 0 until tunnelWidth) {
                    val pos = basePos.offset(right, m).up(n)
                    if (!blockBreakQueue.contains(pos))
                        blockBreakQueue.add(pos)
                }
            }
        }

        var placeCounter = 0
        val blocksToRemoveFromPlaceQueue = mutableListOf<BlockPos>()
        for (blockPos in blockPlaceQueue) {
            val blockChanged = placeBlockIfAir(blockPos)

            blocksToRemoveFromPlaceQueue.add(blockPos)

            if (blockChanged)
                placeCounter++

            if (placeCounter >= amountOfBlocksToPlacePerTick)
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

//        blockBreakQueue.take(1).forEach {
//            if (breakBlockIfNotAir(it).isAccepted)
//                blockPlaceQueue.remove(it)
//        }
    }

    private fun placeBlockIfAir(pos: BlockPos): Boolean {
        val blockState = Client.world!!.getBlockState(pos)
        return if (blockState.isAir || blockState.fluidState.isStill) {
            Client.interactionManager!!.interactBlock(
                Client.player,
                Client.world,
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
            Client.interactionManager!!.sendPlayerAction(
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                pos,
                Direction.DOWN
            )
            Client.interactionManager!!.sendPlayerAction(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                pos,
                Direction.DOWN
            )
            Client.networkHandler!!.sendPacket(HandSwingC2SPacket(Hand.MAIN_HAND))
            true
        } else
            false
    }

    private fun onEnable() {
        Logger.info("Enabling autohighwaybuilder...")

        DelayedAction.register({

        }, 40)

        Client.player?.sendMessage(Text.of("Enabling autohighwaybuilder!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling autohighwaybuilder...")

        blockPlaceQueue.clear()
        blockBreakQueue.clear()

        Client.player?.sendMessage(Text.of("Disabling autohighwaybuilder!"), false)
    }
}