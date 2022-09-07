package modules.menuopeners

import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import screens.ModSettingsScreen
import utils.Global.Client

object CheatMenuOpener : ClientModule, Keybinded {
    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.menu.cheatmenu.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "category.modid.cheat"
        )
    )!!

    override fun onKeybindingPressed() {
        if (Client.currentScreen !is ModSettingsScreen)
            Client.setScreen(ModSettingsScreen(Client.currentScreen, Client.options))
    }
}