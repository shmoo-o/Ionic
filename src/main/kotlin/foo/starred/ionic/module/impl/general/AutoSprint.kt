package foo.starred.ionic.module.impl.general

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.gui.MinecraftGui

@LoadableIonicModule
object AutoSprint : Module(
    "Auto sprint",
    "Automatically sprints.",
    ConfigCategory.GENERAL
) {
    val water by config.switch("Sprint in water")

    val sprint: Boolean
        get() {
            val player = client.player ?: return false

            return enabled && MinecraftGui.screen == null && (water || !player.isInWater)
        }
}
