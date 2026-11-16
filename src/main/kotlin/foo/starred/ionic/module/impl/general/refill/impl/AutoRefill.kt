@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.ionic.module.impl.general.refill.impl

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.ionic.api.messaging.impl.MessagingAPI.displayAsMod
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.MessageEvent
import foo.starred.ionic.module.base.Module
import foo.starred.ionic.module.impl.general.refill.data.AutoRefillItemData
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object AutoRefill : Module(
    "Auto refill",
    "Automatically refills selected items",
    ConfigCategory.GENERAL
) {
    private val dungeons by config.group("Dungeons")
    private val `dungeons$start` by dungeons.switch("Refill on dungeon start", true)
    private val `dungeons$puzzle` by dungeons.switch("Get draft on puzzle fail", true)
    private val `dungeons$start$refill` by dungeons.multiSelector("Items to refill", AutoRefillItemData.entries.map { it.display }, AutoRefillItemData.entries.map { it.ordinal })

    private val kuudra by config.group("Kuudra")
    private val `kuudra$pearl` by kuudra.switch("Refill pearls on supply", true)

    private val kuudraRegex = Regex("(?:\\[[^]]*] )?(?<user>\\w+) recovered one of Elle's supplies! \\(\\d+/\\d+\\)")

    init {
        on<MessageEvent.Chat.Receive> {
            if (!`kuudra$pearl`) return@on

            val name = kuudraRegex.matchEntire(stripped)?.groups["user"]?.value ?: return@on
            if (client.user.name != name) return@on

            AutoRefillItemData.ENDER_PEARL.refill()
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<MessageEvent.Chat.Receive> {
            if (`dungeons$puzzle` && stripped.startsWith("PUZZLE FAIL! ")) {
                "Getting draft on puzzle fail...".displayAsMod()
                MinecraftCommands.client("/gfs architect's first draft 1")
            }

            if (`dungeons$start` && stripped == "[NPC] Mort: Here, I found this map when I first entered the dungeon.") {
                for (entry in AutoRefillItemData.entries) {
                    if (entry.ordinal !in `dungeons$start$refill`) continue
                    entry.refill()
                }
            }
        }.runWhen(PresetSkyBlockIsland.THE_CATACOMBS.state)
    }
}
