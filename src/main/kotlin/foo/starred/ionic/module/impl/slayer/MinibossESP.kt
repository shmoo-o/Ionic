package foo.starred.ionic.module.impl.slayer

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.ionic.event.impl.LocationEvent
import foo.starred.ionic.event.impl.SlayerEvent
import foo.starred.ionic.event.impl.TickEvent
import foo.starred.ionic.event.impl.WorldRenderEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.kontext.api.entity.extension.renderVec3
import foo.starred.parallax.api.primitives.ParallaxBox
import foo.starred.parallax.api.primitives.ParallaxLine
import net.minecraft.world.entity.Entity
import java.util.concurrent.ConcurrentHashMap

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object MinibossESP : Module(
    "Miniboss ESP",
    "Highlights for your minibosses.",
    ConfigCategory.SLAYER
) {
    private val depth by config.switch("Depth check")
    private val tracer by config.switch("Show tracer")
    private val color by config.colorPicker("Highlight color", MochaColorScheme.Text.argb)
    private val width by config.slider("Highlight width", 2f, 1f, 10f)

    private val entities = ConcurrentHashMap.newKeySet<Entity>()

    init {
        on<SlayerEvent.Miniboss.Spawn> {
            entities += slayerInfo.entity
        }

        on<SlayerEvent.Miniboss.Death> {
            entities -= slayerInfo.entity
        }

        on<TickEvent.Client.End> {
            if (ticks % 5 != 0) return@on

            entities.removeIf { !it.isAlive }
        }

        on<LocationEvent.Server.Connect> {
            entities.clear()
        }

        on<WorldRenderEvent.Extract> {
            for (entity in entities) {
                if (!entity.isAlive) continue

                ParallaxBox.frame(entity.renderAABB(), color, width, depth)

                if (tracer) {
                    ParallaxLine.tracer(entity.renderVec3().toVector3f(), color, width)
                }
            }
        }
    }
}
