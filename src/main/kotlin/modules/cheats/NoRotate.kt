package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object NoRotate : Cheat("NoRotate"), Keybinded {
    override val name = TranslatableText("cheat.modid.norotate.name")
    override val description = TranslatableText("cheat.modid.norotate.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.norotate", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(PacketEvent.Receive::class)
    private fun onReceivePacket(event: PacketEvent.Receive) {
        if (!enabled) return
        if (event.packet !is PlayerPositionLookS2CPacket) return

        Client.player?.let { player ->
            event.packet.yaw = player.yaw
            event.packet.pitch = player.pitch
        }
    }
}