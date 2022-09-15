package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.TickEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.DoubleOption
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Option
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import screens.ModSettingsListWidget
import utils.Global.Client

object Flight : Cheat("Flight"), Keybinded {
    override val name = TranslatableText("cheat.modid.flight.name")
    override val description = TranslatableText("cheat.modid.flight.description")

    override val options: List<Option> = listOf(
        DoubleOption(
            "options.modid.flight.flyingSpeed.name",
            0.01,
            1.0,
            0.01f,
            { flyingSpeed },
            { _, value: Double -> flyingSpeed = value },
            ModSettingsListWidget.getDoubleLabel,
            ModSettingsListWidget.getTooltipFromKey("options.modid.flight.flyingSpeed.description"),
        ),
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.flight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.modid.cheat"
        )
    )!!

    private var flyingSpeed = 0.1

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    override fun onEnable() {
        super.onEnable()
        Client.player?.let {
            it.abilities.allowFlying = true
        }
    }

    override fun onDisable() {
        super.onDisable()
        Client.player?.let {
            it.abilities.allowFlying = false
            it.abilities.flying = false
        }
    }

    @EventHandler(TickEvent.Pre::class)
    private fun onTick() {
        if (!enabled) return

        Client.player?.let {
            it.abilities.allowFlying = true
            it.abilities.flySpeed = flyingSpeed.toFloat()
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