package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import utils.Global.Client
import kotlin.math.pow
import kotlin.math.sqrt

@ClientModule
object Speed : Cheat(), Keybinded {
    override val name = Text.translatable("cheat.modid.speed.name")
    override val description = Text.translatable("cheat.modid.speed.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.speed", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(TickEvent.Post::class)
    private fun afterTick() {
        if (!enabled) return

        Client.player?.let {
            // return if sneaking or not walking
            if (it.isSneaking || it.forwardSpeed == 0f && it.sidewaysSpeed == 0f)
                return

            // activate sprint if walking forward
            if (it.forwardSpeed > 0 && !it.horizontalCollision)
                it.isSprinting = true

            // activate mini jump if on ground
            if (!it.isOnGround)
                return

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