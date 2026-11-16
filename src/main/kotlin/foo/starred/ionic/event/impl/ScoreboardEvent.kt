package foo.starred.ionic.event.impl

import foo.starred.ionic.event.core.IonicEvent
import net.minecraft.network.chat.Component

sealed class ScoreboardEvent {
    data class Update(
        val lines0: List<String>,
        val lines1: List<String>,
        val components0: List<Component>,
        val components1: List<Component>,
    ) : IonicEvent() {
        val added: List<String> by lazy(LazyThreadSafetyMode.NONE) {
            lines1 - lines0.toSet()
        }

        val removed: List<String> by lazy(LazyThreadSafetyMode.NONE) {
            lines0 - lines1.toSet()
        }
    }
}
