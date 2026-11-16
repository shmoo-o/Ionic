package foo.starred.ionic.api.messaging.impl

import foo.starred.ionic.api.messaging.enums.MessagePrefixType
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.text.parser.impl.parse
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component

object MessagingAPI {
    @JvmStatic
    @JvmOverloads
    fun String.displayAsMod(prefixType: MessagePrefixType = MessagePrefixType.DEFAULT) {
        parse().displayAsMod(prefixType)
    }

    @JvmStatic
    @JvmOverloads
    fun Component.displayAsMod(prefixType: MessagePrefixType = MessagePrefixType.DEFAULT) {
        val colored = if (this.style.color == null) this.copy().withColor(0xFFFFFF) else this
        MinecraftMessages.display(prefixType.component.copy().append(" ".literal()).append(colored))
    }

    @JvmStatic
    fun String.displayAsDev() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment) return

        displayAsMod(MessagePrefixType.DEV)
    }

    @JvmStatic
    fun Component.displayAsDev() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment) return

        displayAsMod(MessagePrefixType.DEV)
    }
}
