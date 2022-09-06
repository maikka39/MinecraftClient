package event

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

class InvalidFunctionHeaderForEventHandler(func: KFunction<*>, obj: Any) :
    Exception("Invalid parameter count for EventHandler \"${func.name}\" in class \"${obj::class.simpleName}\". Only () and (event: <T: Event>), where T is of the same type as event in in EventHandler(event), are accepted.")

private fun getEventListener(ann: EventHandler, func: KFunction<*>, obj: Any): EventListener<Event> {
    return when (func.parameters.count()) {
        1 -> EventListener { func.call(obj) }
        2 ->
            if (func.parameters[1] == func.extensionReceiverParameter)
                EventListener { func.call(obj, obj) }
            else if (func.parameters[1].type == ann.event.starProjectedType)
                EventListener { func.call(obj, it) }
            else
                throw InvalidFunctionHeaderForEventHandler(func, obj)
        3 ->
            if (func.extensionReceiverParameter == null)
                throw InvalidFunctionHeaderForEventHandler(func, obj)
            else if (func.parameters[2].type != ann.event.starProjectedType)
                throw InvalidFunctionHeaderForEventHandler(func, obj)
            else
                return EventListener { func.call(obj, obj, it) }
        else -> throw InvalidFunctionHeaderForEventHandler(func, obj)
    }
}

@Suppress("UNCHECKED_CAST")
fun registerEventsInObject(obj: Any) {
    val functions = obj::class.functions + obj::class.superclasses.flatMap { it.functions }

    functions.forEach { func ->
        val ann = func.annotations.firstOrNull { it.annotationClass == EventHandler::class } as EventHandler?
            ?: return@forEach

        if (!ann.event.isSubclassOf(Event::class))
            throw Exception("Event of \"${func.name}\" in class \"${obj::class.simpleName}\" must be a subtype of Event")

        func.isAccessible = true

        val eventListener: EventListener<Event> = getEventListener(ann, func, obj)

        EventManager.register(ann.event as KClass<out Event>, eventListener)
    }
}