package event

abstract class CancellableEvent : Event {
    var cancelled: Boolean = false
        private set

    fun cancel() {
        cancelled = true
    }
}