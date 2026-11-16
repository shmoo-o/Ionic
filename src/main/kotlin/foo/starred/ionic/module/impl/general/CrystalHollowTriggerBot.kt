package foo.starred.ionic.module.impl.general

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.InputEvent
import foo.starred.ionic.event.impl.TickEvent
import foo.starred.ionic.module.base.Module
import foo.starred.ionic.util.rightClick
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.client.extension.client
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult

@LoadableIonicModule
@IonicModuleScope(islands = [PresetSkyBlockIsland.CRYSTAL_HOLLOWS])
object CrystalHollowTriggerBot : Module(
    "Crystal hollow triggerbot",
    "Automatically clicks chests when looking at them.",
    ConfigCategory.GENERAL
) {
    private val mode = config.selector("Mode", listOf("Automatic", "Manual (left-click)")).unique("mode")

    private var last: BlockPos? = null

    init {
        on<TickEvent.Client.Start> {
            val position = (client.hitResult as? BlockHitResult)?.blockPos ?: return@on
            if (position == last) return@on

            val block = client.level?.getBlockState(position) ?: return@on
            if (block.block != Blocks.CHEST) return@on

            last = position
            rightClick()
        }.runWhen(mode.state.map { it == 0 })

        on<InputEvent.Mouse.Press> {
            if (buttonInfo.button() != InputConstants.MOUSE_BUTTON_LEFT) return@on

            val position = (client.hitResult as? BlockHitResult)?.blockPos ?: return@on
            val block = client.level?.getBlockState(position) ?: return@on
            if (block.block != Blocks.CHEST) return@on

            cancel()
            rightClick()
        }.runWhen(mode.state.map { it == 1 })
    }
}
