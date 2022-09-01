package modules.menuopeners

import modules.ClientModule
import modules.Keybinded
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import screens.ModSettingsScreen
import utils.Global.Client

class CheatMenuOpener : ClientModule, Keybinded {
    override val keyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.modid.menu.cheatmenu.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "category.modid.menu"
        )
    )!!

    override fun onKeybindingPressed() {
        if (Client.getCurrentScreen() !is ModSettingsScreen)
            Client.setScreen(ModSettingsScreen(Client.getCurrentScreen(), Client.getOptions()))
    }
}