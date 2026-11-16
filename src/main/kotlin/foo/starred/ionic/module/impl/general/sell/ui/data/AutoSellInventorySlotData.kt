package foo.starred.ionic.module.impl.general.sell.ui.data

import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.primitives.impl.ItemPrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.ionic.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.ionic.module.impl.general.sell.impl.AutoSell
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.string.stripped

class AutoSellInventorySlotData(
    val index: Int,
    val box: RoundedRectanglePrimitive,
    val icon: ItemPrimitive
) {
    fun update(hovered: Boolean) {
        val items = client.player?.inventory?.nonEquipmentItems ?: return
        val stack = items.getOrNull(index)?.takeIf { !it.isEmpty }
        if (stack == null) {
            box.color = CascadeGeometricColor(0)
            return
        }

        val name = stack.hoverName.stripped()
        box.color = CascadeGeometricColor(when {
            name.isNotEmpty() && name in AutoSell.allowed.value -> MochaColorScheme.Green.alpha(if (hovered) 0.35f else 0.20f)
            name.isNotEmpty() && name in AutoSell.blocked.value -> MochaColorScheme.Red.alpha(if (hovered) 0.35f else 0.20f)
            else -> MochaColorScheme.Surface0.alpha(if (hovered) 0.60f else 0f)
        })
    }
}
