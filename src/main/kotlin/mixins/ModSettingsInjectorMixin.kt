package mixins

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import screens.ModSettingsScreen
import utils.Global.Client

@Mixin(GameMenuScreen::class)
class ModSettingsInjectorMixin constructor(title: Text?) : Screen(title) {
    @Inject(at = [At("RETURN")], method = ["initWidgets"])
    private fun addModMenu(info: CallbackInfo) {
        val x = width / 2 - 102
        val y = height / 4 + 72 + -16

        val feedbackButton = children().stream().filter { element: Element ->
            val buttonWidget =
                element as ButtonWidget
            buttonWidget.x == x && buttonWidget.y == y
        }.findFirst().get()
        this.remove(feedbackButton)

        addDrawableChild(ButtonWidget(
            x, y, 98, 20, TranslatableText("menu.modid.mod_settings")
        ) {
            client!!.setScreen(
                ModSettingsScreen(this, Client.options)
            )
        })
    }
}