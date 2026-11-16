package foo.starred.ionic.module.impl.render

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.WorldRenderEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.color.MinecraftColors
import foo.starred.kontext.api.entity.extension.renderVec3
import foo.starred.kontext.api.entity.extension.serverHealth
import foo.starred.kontext.api.entity.extension.serverMaxHealth
import foo.starred.kontext.api.format.extension.number.abbreviate
import foo.starred.kontext.api.text.parser.impl.parse
import foo.starred.parallax.api.primitives.ParallaxText
import net.minecraft.client.renderer.entity.state.CreeperRenderState
import net.minecraft.world.entity.monster.Creeper

@LoadableIonicModule
@IonicModuleScope(islands = [PresetSkyBlockIsland.DWARVEN_MINES])
object GhostESP : Module(
    "Ghost ESP",
    "Forces ghosts to render",
    ConfigCategory.RENDER
) {
    private val nametags = config.switch("Show nametags", true).unique("nametags")
    private val powered by config.switch("Show powered layer", true)

    init {
        on<WorldRenderEvent.Entity> {
            val state = state as? CreeperRenderState ?: return@on
            val entity = entity as? Creeper ?: return@on
            if (entity.serverMaxHealth < 1_000_000) return@on

            state.isPowered = powered
            state.isInvisible = false
        }

        on<WorldRenderEvent.Entity> {
            val entity = entity as? Creeper ?: return@on
            val max = entity.serverMaxHealth.takeIf { it >= 1_000_000 } ?: return@on
            val hp = entity.serverHealth
            val runic = max / 4 == 1_000_000f

            val name = if (runic) "<dark_purple>Runic Ghost" else "<red>Ghost"
            ParallaxText.component("<dark_gray>[<gray>???<dark_gray>] $name <white>${hp.abbreviate(0)}<gray>/<white>${max.abbreviate(0)}<red>❤".parse(), entity.renderVec3().add(0.0, entity.bbHeight + 0.5, 0.0), depth = true)
        }.runWhen(nametags.state)
    }
}
