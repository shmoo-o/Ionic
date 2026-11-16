package foo.starred.ionic.event.impl

import foo.starred.ionic.api.location.area.base.ISkyBlockArea
import foo.starred.ionic.api.location.island.base.ISkyBlockIsland
import foo.starred.ionic.event.core.IonicEvent
import foo.starred.kbus.data.event.traits.KBusUnconditionalTrait
import net.hypixel.data.type.ServerType

sealed class LocationEvent {
    sealed class Hypixel {
        data class Server(
            val name: String,
            val type: ServerType?,
            val lobby: String?,
            val mode: String?,
            val map: String?,
        ) : IonicEvent()
    }

    sealed class SkyBlock {
        data class Island(
            val old: ISkyBlockIsland,
            val new: ISkyBlockIsland
        ) : IonicEvent(), KBusUnconditionalTrait

        data class Area(
            val old: ISkyBlockArea,
            val new: ISkyBlockArea
        ) : IonicEvent(), KBusUnconditionalTrait

        data object Connect : IonicEvent()

        data object Disconnect : IonicEvent()
    }

    sealed class Server {
        data object Connect : IonicEvent(), KBusUnconditionalTrait

        data object Disconnect : IonicEvent(), KBusUnconditionalTrait
    }
}
