package foo.starred.ionic.module.impl.general.experiment.solvers.base

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

interface IExperimentSolver {
    val next: Int?
    val close: Boolean

    fun fn(screen: AbstractContainerScreen<*>)
    fun reset()
}
