package event

import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

@Suppress("UNCHECKED_CAST")
fun registerEventsInObject(obj: Any) {
    val functions = obj::class.functions + obj::class.superclasses.flatMap { it.functions }

    functions.forEach { func ->
        val ann = func.annotations.firstOrNull { it.annotationClass == EventHandler::class } as EventHandler?
            ?: return@forEach

        if (!ann.event.isSubclassOf(Event::class)) throw Exception("Event of \"${func.name}\" in class \"${obj::class.simpleName}\" must be a subtype of Event")

        func.isAccessible = true

        val eventListener: EventListener<Event> = if (func.parameters.count() == 1)
            EventListener { func.call(obj) }
        else if (func.parameters.count() == 2 && func.parameters[1].type == ann.event.starProjectedType)
            EventListener { func.call(obj, it) }
        else if (func.parameters.count() == 2 && func.extensionReceiverParameter != null)
            EventListener { func.call(obj, obj) }
        else if (func.parameters.count() == 2 && func.extensionReceiverParameter != null && func.parameters[2].type == ann.event.starProjectedType)
            EventListener { func.call(obj, obj, it) }
        else
            throw Exception("Invalid parameter count for EventHandler \"${func.name}\" in class \"${obj::class.simpleName}\". Only () and (event: <T: Event>), where T is of the same type as event in in EventHandler(event), are accepted.")

        EventManager.register(ann.event as KClass<out Event>, eventListener)
    }
}