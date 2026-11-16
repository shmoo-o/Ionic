package foo.starred.ionic.module.impl.general

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.time.IonicScheduler
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.MessageEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.abstraction.chat.color.MinecraftColors
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.component.colored
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks
import foo.starred.kontext.api.scheduling.scheduler.extensions.start
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object AutoConversation : Module(
    "Auto conversation",
    "Automatically proceeds with conservations for you.",
    ConfigCategory.GENERAL
) {
    private val multi by config.switch("Multi-option dialogues", true)
    private val green by config.switch("Check green color", true)
    private val delay by config.slider("Click delay", 4, 0, 40, "ticks")

    init {
        on<MessageEvent.Chat.Receive> {
            if (!stripped.startsWith("[NPC] ") && !stripped.startsWith("Select an option: ")) return@on
            val list = mutableListOf<ClickEvent.Custom>()

            val root = message.style.clickEvent as? ClickEvent.Custom
            if (root != null && green(message)) list += root

            for (sibling in message.siblings) {
                val event = sibling.style.clickEvent as? ClickEvent.Custom ?: continue
                if (!green(sibling)) continue

                list += event
            }

            if (list.isEmpty()) return@on
            if (list.size > 1 && !multi) return@on

            val event = list.first()
            val packet = ServerboundCustomClickActionPacket(event.id(), event.payload())

            if (delay == 0) {
                client.connection?.send(packet)
                return@on
            }

            IonicScheduler.schedule((delay + (0..3).random()).clientTicks.start) {
                client.connection?.send(packet)
            }
        }
    }

    private fun green(component: Component): Boolean {
        return !green || component.style.color?.value == MinecraftColors.GREEN.rgb || component.colored().substringAfterLast("[").startsWith("§a")
    }
}
