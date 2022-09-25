package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import mixinterfaces.IPlayerMoveC2SPacket
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import utils.Global.Client

object NoFall : Cheat("NoFall"), Keybinded {
    override val name = TranslatableText("cheat.modid.nofall.name")
    override val description = TranslatableText("cheat.modid.nofall.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.nofall", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerMoveC2SPacket) return
        val packet = event.packet

        packet as IPlayerMoveC2SPacket
        if (packet.isMine) return

        val player = Client.player!!

        if (player.velocity.y >= -0.08) return

        packet.onGround = true
    }
}