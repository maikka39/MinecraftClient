package mixins

import mixinterfaces.IMultiplayerScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ServerInfo
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import utils.LastServerRememberer

@Mixin(MultiplayerScreen::class)
abstract class MultiplayerScreenMixin(title: Text?) : Screen(title), IMultiplayerScreen {
    @Shadow
    private fun connect(entry: ServerInfo) {
    }

    override fun connectToServer(server: ServerInfo) {
        connect(server)
    }

    @Inject(at = [At("HEAD")], method = ["connect(Lnet/minecraft/client/network/ServerInfo;)V"])
    private fun onConnect(entry: ServerInfo, ci: CallbackInfo) {
        LastServerRememberer.lastServer = entry
    }
}