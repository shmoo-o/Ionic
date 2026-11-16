package foo.starred.ionic.module.impl.slayer

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.messaging.impl.MessagingAPI.displayAsMod
import foo.starred.ionic.api.slayer.SlayerAPI
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.InputEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.abstraction.client.extension.boundKey
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.input.impl.GenericInputState
import net.minecraft.world.phys.EntityHitResult

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object AntiBossGrief : Module(
    "Anti boss grief",
    "Prevents you from hitting other player's bosses",
    ConfigCategory.SLAYER
) {
    private val chat by config.switch("Send message on cancel")
    private val override by config.keybind("Override key")

    init {
        on<InputEvent.Mouse.Press> {
            if (client.options.keyAttack.boundKey().value != buttonInfo.button()) return@on

            val entity = (client.hitResult as? EntityHitResult)?.entity ?: return@on
            val slayer = SlayerAPI.bosses[entity] ?: return@on
            if (slayer.owned) return@on
            if (GenericInputState.pressed(override)) return@on

            cancel()

            if (chat) {
                "<red>Cancelled hit on <aqua>${slayer.owner}<r>'s boss!".displayAsMod()
            }
        }
    }
}
