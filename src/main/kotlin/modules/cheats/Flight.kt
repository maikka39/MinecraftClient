package modules.cheats

import event.EventHandler
import events.packets.PacketEvent
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import utils.options.Option
import utils.options.OptionCallbacks.Companion.ValidatingDoubleSliderCallbacks
import utils.options.TooltipFactory
import utils.options.ValueTextFactory

@ClientModule
object Flight : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.flight.name")
    override val description = Text.translatable("cheat.modid.flight.description")

    private val flyingSpeed = Option<Double>(
        "options.modid.flight.flyingSpeed.name",
        TooltipFactory.fromKey("options.modid.flight.flyingSpeed.description"),
        ValueTextFactory.roundedDouble(2),
        ValidatingDoubleSliderCallbacks(0.01, 1.0, 0.01),
        0.1,
    )

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.flight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.modid.cheat"
        )
    )!!

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
            it.abilities.flySpeed = flyingSpeed.value.toFloat()
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