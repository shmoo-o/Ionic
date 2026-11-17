package foo.starred.ionic.module.impl.slayer

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.slayer.SlayerAPI
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.ionic.event.impl.WorldRenderEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.kontext.api.entity.extension.renderVec3
import foo.starred.parallax.api.primitives.ParallaxBox
import foo.starred.parallax.api.primitives.ParallaxLine

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object BossESP : Module(
    "Boss ESP",
    "Highlights for your slayer boss",
    ConfigCategory.SLAYER
) {
    private val depth by config.switch("Depth check")
    private val tracer by config.switch("Show tracer")
    private val color by config.colorPicker("Highlight color", MochaColorScheme.Text.argb)
    private val width by config.slider("Highlight width", 2f, 1f, 10f)

    init {
        on<WorldRenderEvent.Extract> {
            val entity = SlayerAPI.slayer?.entity ?: return@on
            if (!entity.isAlive) return@on

            ParallaxBox.frame(entity.renderAABB(), color, width, depth)

            if (tracer) {
                ParallaxLine.tracer(entity.renderVec3().toVector3f(), color, width)
            }
        }
    }
}
