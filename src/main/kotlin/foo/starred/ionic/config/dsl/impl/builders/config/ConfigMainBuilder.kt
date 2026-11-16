package foo.starred.ionic.config.dsl.impl.builders.config

import foo.starred.ionic.Ionic
import foo.starred.ionic.config.ConfigManager
import foo.starred.ionic.config.dsl.base.ConfigScope
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.api.data.Observable

class ConfigMainBuilder(
    val key: String,
    name: String,
    description: String,
    category: ConfigCategory,
    private val default: Boolean = false
) : ConfigScope {
    val feature = ConfigManager.feature(name, description, category, key, default)
    val state = Observable(default)

    override val builder: ConfigMainBuilder
        get() = this

    val value: Boolean
        get() = state.value

    var module: Module? = null
        internal set

    init {
        Ionic.LOGGER.debug("Feature added for {}: {}", key, feature)

        ConfigManager.observe(key) {
            state.value = it as? Boolean ?: default
        }
    }
}
