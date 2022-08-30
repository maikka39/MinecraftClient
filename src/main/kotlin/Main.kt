import utils.Global.Cheats
import mu.KotlinLogging
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

private val logger = KotlinLogging.logger {}

@Suppress("unused")
fun init() {
    logger.info("Init")
}

@Suppress("unused")
@Environment(EnvType.CLIENT)
fun clientInit() {
    logger.info("Client init")

    logger.info("Initializing cheats")
    Cheats.forEach { cheat ->
        logger.info("Initializing ${cheat.name.string}...")
        cheat.initialize()
    }
    logger.info("Finished initializing cheats")
}