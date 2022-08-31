package event

fun interface EventListener<in T : Event> {
    fun handle(event: T)
}