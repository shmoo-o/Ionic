package foo.starred.ionic.module.impl.general.wardrobe.auto.data

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key0
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key1
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key2
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key3
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key4
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key5
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key6
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key7
import foo.starred.ionic.module.impl.general.wardrobe.auto.impl.AutoWardrobe.key8
import foo.starred.kontext.abstraction.client.extension.client
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

data class AutoWardrobeSlot(
    val index: Int,
    val keybind: () -> InputConstants.Key
) {
    val slot: Slot?
        get() = client.player?.containerMenu?.slots?.getOrNull(index)

    val equipped: Boolean
        //~ if >= 26.2 'Items.LIME_DYE' -> 'Items.DYE.lime()'
        get() = slot?.item?.item == Items.LIME_DYE

    companion object {
        val ALL = listOf(
            AutoWardrobeSlot(36) { key0 },
            AutoWardrobeSlot(37) { key1 },
            AutoWardrobeSlot(38) { key2 },
            AutoWardrobeSlot(39) { key3 },
            AutoWardrobeSlot(40) { key4 },
            AutoWardrobeSlot(41) { key5 },
            AutoWardrobeSlot(42) { key6 },
            AutoWardrobeSlot(43) { key7 },
            AutoWardrobeSlot(44) { key8 }
        )
    }
}
