package foo.starred.ionic.api.slayer.enums.type.impl

import foo.starred.ionic.api.slayer.enums.type.base.ISlayerType

enum class SlayerDemon(override val display: String, override val names: Set<String> = setOf(display)) : ISlayerType {
    Quazii("ⓆⓊⒶⓏⒾⒾ"),
    Typhoeus("ⓉⓎⓅⒽⓄⒺⓊⓈ")
}
