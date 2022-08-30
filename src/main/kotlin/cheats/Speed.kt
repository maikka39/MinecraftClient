package cheats

import utils.Global.Client
import cheats.interfaces.Cheat
import cheats.interfaces.Keybinded
import mu.KotlinLogging
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import kotlin.math.pow
import kotlin.math.sqrt

private val logger = KotlinLogging.logger {}

class Speed : Cheat, Keybinded {
    override var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    override val name = TranslatableText("cheat.modid.speed.name")
    override val description = TranslatableText("cheat.modid.speed.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.speed", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.modid.cheat"
        )
    )!!

    override fun initialize() {
        super.initialize()

        ClientTickEvents.END_CLIENT_TICK.register {
            if (!enabled) return@register

            Client.getPlayer()?.let {
                // return if sneaking or not walking
                if (it.isSneaking || it.forwardSpeed == 0f && it.sidewaysSpeed == 0f
                ) return@let

                // activate sprint if walking forward
                if (it.forwardSpeed > 0 && !it.horizontalCollision) it.isSprinting = true

                // activate mini jump if on ground
                if (!it.isOnGround) return@let

                var v: Vec3d = it.velocity
                it.setVelocity(v.x * 1.8, v.y + 0.1, v.z * 1.8)

                v = it.velocity
                val currentSpeed = sqrt(v.x.pow(2.0) + v.z.pow(2.0))

                val maxSpeed = 0.66

                if (currentSpeed > maxSpeed) it.setVelocity(
                    v.x / currentSpeed * maxSpeed, v.y,
                    v.z / currentSpeed * maxSpeed
                )
            }
        }
    }

    private fun onEnable() {
        logger.info("Enabling speed...")

        Client.getPlayer()?.sendMessage(Text.of("Enabling speed!"), false)
    }

    private fun onDisable() {
        logger.info("Disabling speed...")

        Client.getPlayer()?.sendMessage(Text.of("Disabling speed!"), false)
    }
}