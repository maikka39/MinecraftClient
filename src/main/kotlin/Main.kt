import event.EventManager
import event.registerEventsInObject
import events.client.InitializeEvent
import mu.KotlinLogging
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import utils.DelayedAction
import utils.Global.ClientModules

val Logger = KotlinLogging.logger {}

@Suppress("unused")
fun init() {
    Logger.info("Init")
    DelayedAction // Initialize object
    ClientModules.forEach { registerEventsInObject(it) }
    EventManager.notify(InitializeEvent())
}

@Suppress("unused")
@Environment(EnvType.CLIENT)
fun clientInit() {
    Logger.info("Client init")
    EventManager.notify(InitializeEvent.Client())
}