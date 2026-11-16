package foo.starred.ionic.config.data.impl

import foo.starred.ionic.config.data.base.IConfigElementData

data class ConfigButtonElementData(
    override val name: String,
    override val key: String,
    val function: () -> Unit,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigButtonElementData {
        return copy(key = key, parent = parent, description = description)
    }
}
