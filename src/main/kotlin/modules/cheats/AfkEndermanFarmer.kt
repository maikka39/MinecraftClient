package modules.cheats

import event.EventHandler
import events.world.TickEvent
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW
import utils.Global.Client

class AfkEndermanFarmer : Cheat, Keybinded {
    override var enabled = false

    override val name = TranslatableText("cheat.modid.afkendermanfarmer.name")
    override val description = TranslatableText("cheat.modid.afkendermanfarmer.description")

    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.cheat.afkendermanfarmer", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        enabled = !enabled
    }

    @EventHandler(TickEvent.Post::class)
    private fun onTick() {
        if (!enabled) return

        val cooldownProgress = Client.player?.getAttackCooldownProgress(0f)

        if (cooldownProgress == null || cooldownProgress < 1) return

        Client.world?.let { world ->
            val targetEntities = world.entities
                .filterIsInstance<EndermanEntity>()
                .map { Pair(it.pos.distanceTo(Client.player!!.pos), it) }
                .filter { it.first < Client.interactionManager!!.reachDistance }

            val entityToAttack = targetEntities.minByOrNull { it.first }?.second

            entityToAttack?.let {
                Client.interactionManager!!.attackEntity(Client.player!!, it)
                Client.player!!.swingHand(Hand.MAIN_HAND)
            }
        }
    }
}