package modules.cheats

import Logger
import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.apache.commons.lang3.tuple.MutablePair
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.Global.Client
import java.util.function.Function
import kotlin.math.abs

object LOAntiKick : Cheat, Keybinded {
    override var enabled = true
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.loantikick.amountOfAxisToLockOn.name",
            0.0,
            2.0,
            1.0f,
            { amountOfAxisToLockOn.toDouble() },
            { _, value: Double -> amountOfAxisToLockOn = value.toInt() },
            ModSettingsListWidget.getIntLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.loantikick.amountOfAxisToLockOn.description"),
        ),
    )

    override val name = TranslatableText("cheat.modid.loantikick.name")
    override val description = TranslatableText("cheat.modid.loantikick.description")
    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.loantikick", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    private var amountOfAxisToLockOn = 2

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return

        if (event.packet is PlayerMoveC2SPacket)
            modify(event.packet)
        else if (event.packet is VehicleMoveC2SPacket)
            modify(event.packet)
    }

    private fun onEnable() {
        Logger.info("Enabling loantikick...")

        Client.player?.sendMessage(Text.of("Enabling loantikick!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling loantikick...")

        Client.player?.sendMessage(Text.of("Disabling loantikick!"), false)
    }

    private var lastX = 0.0
    private var lastZ = 0.0

    private fun modify(packet: PlayerMoveC2SPacket) {
        val player = Client.player!!
        var x: Double = packet.getX(player.x)
        var z: Double = packet.getZ(player.z)

        val startX = x
        val startZ = z

        if (amountOfAxisToLockOn > 0) {
            val coords = calculateNewCoordinate(x, z)
            x = coords.first
            z = coords.second
        }

        if (hasCollisions(x - startX, z - startZ)) {
            for ((endX, endZ) in getAllowedCoordinates(startX, startZ)) {
                if (!hasCollisions(endX - startX, endZ - startZ)) {
                    x = endX
                    z = endZ
                    break
                }
            }
        }

        packet.x = x
        packet.z = z
        lastX = x
        lastZ = z
    }

    private fun modify(packet: VehicleMoveC2SPacket) {
        if (amountOfAxisToLockOn == 0) return

        val (x, z) = calculateNewCoordinate(packet.x, packet.z)

        packet.x = x
        packet.z = z
        lastX = x
        lastZ = z
    }

    private fun calculateNewCoordinate(x: Double, z: Double): Pair<Double, Double> {
        var x1 = x
        var z1 = z

        var xoffset = if (x1 > lastX) -0.00995 else 0.01005
        var zoffset = if (z1 > lastZ) -0.00995 else 0.01005

        if (abs(x1 - lastX) > 10 || abs(z1 - lastZ) > 10) {
            xoffset = 0.0
            zoffset = 0.0
        }

        x1 = (x1 * 100.0).toInt() / 100.0 + xoffset
        z1 = (z1 * 100.0).toInt() / 100.0 + zoffset

        return fixCoordinate(x1, z1)
    }

    private fun hasCollisions(x: Double, z: Double): Boolean {
        return Client.world!!.getBlockCollisions(
            null, Client.player!!.boundingBox.offset(x, 0.0, z)
        ).iterator().hasNext()
    }

    private fun fixCoordinate(x: Double, z: Double): Pair<Double, Double> {
        if (!isCoordinateAllowed(x, z)) return Pair(x, z)

        val coords = MutablePair(
            if (x - lastX < 0) fixDouble(x, Math::ceil) else fixDouble(x, Math::floor),
            if (z - lastZ < 0) fixDouble(z, Math::ceil) else fixDouble(z, Math::floor)
        )

        if (amountOfAxisToLockOn == 1) {
            val xInRange = isInRange(coords.left, x, lastX)
            val zInRange = isInRange(coords.right, z, lastZ)

            if (xInRange && zInRange || !xInRange && !zInRange) {
                if (abs(x - coords.left) < abs(z - coords.right)) coords.left = x
                else coords.right = z
            } else if (xInRange) coords.right = z
            else coords.left = x
        }

        return coords.toPair()
    }

    private fun getAllowedCoordinates(x: Double, z: Double): List<Pair<Double, Double>> {
        return if (!isCoordinateAllowed(x, z)) listOf(
            Pair(fixDouble(x, Math::ceil), fixDouble(z, Math::ceil)),
            Pair(fixDouble(x, Math::ceil), fixDouble(z, Math::floor)),
            Pair(fixDouble(x, Math::floor), fixDouble(z, Math::ceil)),
            Pair(fixDouble(x, Math::floor), fixDouble(z, Math::floor)),
            Pair(fixDouble(x, Math::ceil), z),
            Pair(fixDouble(x, Math::floor), z),
            Pair(x, fixDouble(z, Math::ceil)),
            Pair(x, fixDouble(z, Math::floor)),
        )
        else listOf(Pair(x, z))
    }

    private fun isInRange(value: Double, min: Double, max: Double): Boolean {
        return (value in min..max) || (value in max..min)
    }

    private fun isCoordinateAllowed(deltaX: Double, deltaZ: Double): Boolean {
        return isCoordinateValueAllowed(deltaX) || isCoordinateValueAllowed(deltaZ)
    }

    private fun isCoordinateValueAllowed(delta: Double): Boolean {
        return (delta * 1000).toInt() % 10 == 0
    }

    private fun fixDouble(double: Double, roundFunction: (d: Double) -> Double): Double {
        var power = 34
        var v = (1L shl power).toDouble()
        while (!isCoordinateValueAllowed(roundFunction(double * v) / v) && power > 0) {
            power--
            v = (1L shl power).toDouble()
        }
        return roundFunction(double * v) / v
    }
}