package utils

import modules.ClientModule
import modules.cheats.Cheat
import net.minecraft.client.MinecraftClient
import org.reflections.Reflections
import org.reflections.scanners.Scanners.TypesAnnotated

object Global {
    val Client: MinecraftClient = MinecraftClient.getInstance()

    val Modules: List<Any> = Reflections("modules").get(
        TypesAnnotated
            .with(ClientModule::class.java)
            .asClass<Any>()
            .map { it.kotlin.objectInstance }
            .filter { it != null }
            .map { it as Any }
    ).toList().sortedBy { it::class.simpleName }

    val Cheats: List<Cheat> = Modules.filterIsInstance<Cheat>()
}
