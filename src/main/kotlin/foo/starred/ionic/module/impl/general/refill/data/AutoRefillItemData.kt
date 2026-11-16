package foo.starred.ionic.module.impl.general.refill.data

import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.impl.string.StringFormatter
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver

enum class AutoRefillItemData(val max: Int) {
    SPIRIT_LEAP(16),
    SUPERBOOM_TNT(64),
    INFLATABLE_JERRY(64),
    ENDER_PEARL(16);

    val display: String = StringFormatter.titleCased(name)

    fun refill() {
        val count = client.player?.inventory?.find { it.get<GenericKontextItemDataResolver.ID, String>() == name }?.count ?: 0
        if (count >= max) return

        MinecraftCommands.client("/gfs $name ${max - count}")
    }
}
