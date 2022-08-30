package mixins

import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ServerInfo
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import utils.LastServerRememberer

@Mixin(DirectConnectScreen::class)
class DirectConnectScreenMixin(title: Text?) : Screen(title) {
    @Shadow
    @Final
    private val serverEntry: ServerInfo? = null

    @Inject(at = [At("TAIL")], method = ["saveAndClose()V"])
    private fun onSaveAndClose(ci: CallbackInfo) {
        LastServerRememberer.lastServer = serverEntry
    }
}