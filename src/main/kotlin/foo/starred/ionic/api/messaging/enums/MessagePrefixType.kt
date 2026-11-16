package foo.starred.ionic.api.messaging.enums

import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.network.chat.Component

enum class MessagePrefixType(val component: Component) {
    DEFAULT("<#C2CEFF>[<#A5AFFF>I<#ABD3FF>o<#B8A5FF>n<#A1B3FF>i<#C2BCFF>c<#A1B3FF>]".parse()),
    DEV("<#463F3A>[<#68605B>I<#8A817C>o<#BCB8B1>n<#D8D6D0>i<#F4F3EE>c<#E0AFA0>]".parse())
}
