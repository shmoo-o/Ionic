package foo.starred.ionic.config.data.impl

import foo.starred.ionic.config.data.base.IConfigElementData

data class ConfigInformationElementData(
    override val name: String = "Information",
    override val key: String,
    val text: String,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigInformationElementData {
        return copy(key = key, parent = parent, description = description)
    }
}
