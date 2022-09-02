package utils

import event.EventManager
import events.world.TickEvent

typealias Action = () -> Unit

object DelayedAction {
    private var currentTick = 0
    private var actions: MutableList<Pair<Int, Action>> = mutableListOf()

    init {
        EventManager.register(TickEvent.Pre::class) {
            currentTick++

            val actionsToRun = actions.filter { it.first == currentTick }
            actionsToRun.forEach { it.second() }
            actions.removeAll(actionsToRun)
        }
    }

    fun register(action: Action, delayTicks: Int) {
        actions.add(Pair(currentTick + delayTicks, action))
    }
}
