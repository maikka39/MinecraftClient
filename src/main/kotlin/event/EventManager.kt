package event

import kotlin.reflect.KClass

object EventManager {
    private val listeners: MutableMap<KClass<out Event>, MutableList<EventListener<Event>>> = mutableMapOf()

    inline fun <reified T : Event> register(listener: EventListener<T>) {
        register(T::class, listener)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> register(eventClass: KClass<out T>, listener: EventListener<T>) {
        val eventListeners = listeners.getOrPut(eventClass) { ArrayList() }
        eventListeners.add(listener as EventListener<Event>)
    }

    fun notify(event: Event): Event {
        listeners[event::class]?.forEach { it.handle(event) }
        return event
    }

    fun notify(event: CancellableEvent): CancellableEvent {
        listeners[event::class]?.forEach {
            it.handle(event)
            if (event.cancelled) return event
        }
        return event
    }
}
