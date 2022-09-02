package modules.cheats

import Logger
import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

class Flight : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.flight.name")
    override val description = TranslatableText("cheat.modid.flight.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.flight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private fun onEnable() {
        Logger.info("Enabling flying mode...")

        Client.player?.let {
            it.sendMessage(Text.of("Enabling flying!"), false)

            it.abilities.allowFlying = true
        }
    }

    private fun onDisable() {
        Logger.info("Disabling flying mode...")

        Client.player?.let {
            it.sendMessage(Text.of("Disabling flying!"), false)

            it.abilities.allowFlying = false
            it.abilities.flying = false
        }
    }

    private var lastModifiedTime: Long = 0
    private var lastY = Double.MAX_VALUE

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        val currentTime = System.currentTimeMillis()
        val currentY: Double = packet.getY(Double.MAX_VALUE)

        if (currentY != Double.MAX_VALUE) {
            if (currentTime - lastModifiedTime > 1000 &&
                lastY != Double.MAX_VALUE &&
                Client.world!!.getBlockState(Client.player!!.blockPos.down()).isAir
            ) {
                packet.y = lastY - 0.03130
                lastModifiedTime = currentTime
            } else {
                lastY = currentY
            }
        }
    }
}