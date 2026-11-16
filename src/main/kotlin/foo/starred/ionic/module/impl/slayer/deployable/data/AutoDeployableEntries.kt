package foo.starred.ionic.module.impl.slayer.deployable.data

import foo.starred.kontext.abstraction.inventory.MinecraftInventory
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver

enum class AutoDeployableEntries {
    SOS_FLARE,
    ALERT_FLARE,
    PLASMAFLUX_POWER_ORB,
    OVERFLUX_POWER_ORB,
    WARNING_FLARE,
    MANA_FLUX_POWER_ORB,
    RADIANT_POWER_ORB;

    companion object {
        fun find(): Int? {
            val inventory = MinecraftInventory.instance ?: return null

            for (i in 0..8) {
                val id = inventory.getItem(i).get<GenericKontextItemDataResolver.ID, String>() ?: continue
                if (entries.none { it.name == id }) continue

                return i
            }

            return null
        }
    }
}
