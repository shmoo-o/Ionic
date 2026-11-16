package foo.starred.ionic.event.impl

import foo.starred.ionic.api.slayer.data.SlayerInfo
import foo.starred.ionic.event.core.IonicEvent
import net.minecraft.world.entity.Entity

sealed class SlayerEvent {
    sealed class Boss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()
    }

    sealed class Miniboss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()
    }

    sealed class Demon {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : IonicEvent()
    }

    sealed class Quest {
        data object Start : IonicEvent()

        data object End : IonicEvent()
    }

    sealed class Reset : IonicEvent() {
        data object QuestFail : Reset()

        data object ServerChange : Reset()

        data object Any : Reset()
    }
}
