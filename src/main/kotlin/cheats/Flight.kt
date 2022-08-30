package cheats

import utils.Global.Client
import cheats.interfaces.Cheat
import cheats.interfaces.Keybinded
import mu.KotlinLogging
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW

private val logger = KotlinLogging.logger {}

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

    private fun onEnable() {
        logger.info("Enabling flying mode...")

        Client.getPlayer()?.let {
            it.sendMessage(Text.of("Enabling flying!"), false)

            it.abilities.allowFlying = true
        }
    }

    private fun onDisable() {
        logger.info("Disabling flying mode...")

        Client.getPlayer()?.let {
            it.sendMessage(Text.of("Disabling flying!"), false)

            it.abilities.allowFlying = false
            it.abilities.flying = false
        }
    }


    override fun initialize() {
        super.initialize()

        startKickPreventionLoop()
    }

    private fun startKickPreventionLoop() {
        var counter = 0

        ClientTickEvents.END_CLIENT_TICK.register {
            Client.getPlayer()?.let { player ->
//                enabled = player.abilities.allowFlying == true

                if (!enabled || !player.abilities.flying) return@register

                counter++

                if (counter > 20) {
                    sendDescentPacket()
                    counter = 0
                }
            }
        }
    }

    private fun sendDescentPacket() {
        val player = Client.getPlayer()!!
        Client.getNetworkHandler()!!.sendPacket(
            PlayerMoveC2SPacket.PositionAndOnGround(
                player.x, player.y - 0.4, player.z, player.isOnGround
            )
        )
    }
}