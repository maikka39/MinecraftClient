package mixins

import net.minecraft.client.gui.screen.DisconnectedScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import utils.LastServerRememberer


@Mixin(DisconnectedScreen::class)
abstract class DisconnectedScreenMixin(title: Text?) : Screen(title) {
    @Shadow
    @Final
    private val parent: Screen? = null

    @Shadow
    private val reasonHeight = 0

    @Inject(at = [At("TAIL")], method = ["init()V"])
    private fun onInit(ci: CallbackInfo) {
        addReconnectButtons();
    }

    private fun addReconnectButtons() {
        val backButtonX = width / 2 - 100
        val backButtonY = (height / 2 + reasonHeight / 2 + 9).coerceAtMost(height - 30)

        addDrawableChild(ButtonWidget(
            backButtonX, backButtonY + 24, 200,
            20, LiteralText("Reconnect")
        ) {
            LastServerRememberer.reconnect(parent)
        })
    }

}