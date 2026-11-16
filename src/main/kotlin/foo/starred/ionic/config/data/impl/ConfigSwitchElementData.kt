package foo.starred.ionic.config.data.impl

import foo.starred.ionic.config.data.base.IConfigElementData

data class ConfigSwitchElementData(
    override val name: String,
    override val key: String,
    val default: Boolean,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigSwitchElementData {
        return copy(key = key, parent = parent, description = description)
    }
}
