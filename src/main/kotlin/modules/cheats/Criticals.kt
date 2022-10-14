package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import mixinterfaces.IPlayerMoveC2SPacket
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractType
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client

@ClientModule
object Criticals : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.criticals.name")
    override val description = Text.translatable("cheat.modid.criticals.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.criticals", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    } 

    @EventHandler(PacketEvent.Send::class)
    private fun onSendPacket(event: PacketEvent.Send) {
        if (!enabled) return
        if (event.packet !is PlayerInteractEntityC2SPacket) return

        if (event.packet.type.type != InteractType.ATTACK) return

        Client.player?.let { player ->
            val world = Client.world!!

            if (!player.isOnGround && player.isSubmergedInWater && player.isInLava && player.isClimbing) return

            val entity = world.getEntityById(event.packet.entityId)

            if (entity !is LivingEntity) return

            sendPacket(player, 0.11)
            sendPacket(player, 0.1100013579)
            sendPacket(player, 0.0000013579)
        }

    }

    private fun sendPacket(player: ClientPlayerEntity, yDiff: Double) {
        val packet = PlayerMoveC2SPacket.PositionAndOnGround(player.x, player.y + yDiff, player.z, false)

        @Suppress("KotlinConstantConditions")
        (packet as IPlayerMoveC2SPacket).isMine = true

        player.networkHandler.sendPacket(packet)
    }
}