package modules.cheats.liveoverflow

import event.EventHandler
import events.packets.PacketEvent
import events.world.WorldEvent
import modules.ClientModule
import modules.Keybinded
import modules.cheats.Cheat
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
import net.minecraft.network.packet.s2c.play.*
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client

@ClientModule
object LOPlayBypass : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.liveoverflow.loplaybypass.name")
    override val description = Text.translatable("cheat.modid.liveoverflow.loplaybypass.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.liveoverflow.loplaybypass",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    private var worldJoinTimestamp: Long = 0

    @EventHandler(WorldEvent.Join::class)
    private fun onJoin() {
        worldJoinTimestamp = System.currentTimeMillis()
    }

    @EventHandler(PacketEvent.Receive::class)
    private fun onPacketReceive(event: PacketEvent.Receive) {
        if (!enabled) return
        val packet = event.packet

        if (packet is GameStateChangeS2CPacket) return handleGameStateChangeEvent(event, packet)
        if (packet is GameMessageS2CPacket) return handleGameMessageEvent(event, packet)
        if (packet is WorldBorderInitializeS2CPacket) return event.cancel()
    }

    private fun handleGameStateChangeEvent(event: PacketEvent.Receive, packet: GameStateChangeS2CPacket) {
        when (packet.reason) {
            GameStateChangeS2CPacket.GAME_WON -> {
                event.cancel()
                Client.networkHandler!!.sendPacket(
                    ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN)
                )
            }

            GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN -> {
                event.cancel()
            }

            GameStateChangeS2CPacket.GAME_MODE_CHANGED -> {
                if (System.currentTimeMillis() - worldJoinTimestamp < 3000) event.cancel()
            }
        }
    }

    private fun handleGameMessageEvent(event: PacketEvent.Receive, packet: GameMessageS2CPacket) {
        if (packet.content.string == "You reach the end of Demo!") return event.cancel()
    }
}
