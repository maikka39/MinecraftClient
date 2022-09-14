package modules.cheats

import Logger
import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object Blink : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val options: List<Option> = listOf()

    override val name = TranslatableText("cheat.modid.blink.name")
    override val description = TranslatableText("cheat.modid.blink.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.blink", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private val packets: MutableList<PlayerMoveC2SPacket> = mutableListOf()

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        event.cancel()
        val packet = event.packet

        val prev = packets.lastOrNull()

        if (prev != null &&
            packet.isOnGround == prev.isOnGround &&
            packet.getYaw(-1f) == prev.getYaw(-1f) &&
            packet.getPitch(-1f) == prev.getPitch(-1f) &&
            packet.getX(-1.0) == prev.getX(-1.0) &&
            packet.getY(-1.0) == prev.getY(-1.0) &&
            packet.getZ(-1.0) == prev.getZ(-1.0)
        ) return

        packets.add(packet)
    }

    private fun onEnable() {
        Logger.info("Enabling blink...")

        Client.player?.sendMessage(Text.of("Enabling blink!"), false)
    }

    private fun onDisable() {
        Logger.info("Disabling blink...")

        packets.forEach(Client.player!!.networkHandler::sendPacket)
        packets.clear()

        Client.player?.sendMessage(Text.of("Disabling blink!"), false)
    }
}