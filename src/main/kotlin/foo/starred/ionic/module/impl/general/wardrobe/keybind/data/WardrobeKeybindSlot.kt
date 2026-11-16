package foo.starred.ionic.module.impl.general.wardrobe.keybind.data

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key0
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key1
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key2
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key3
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key4
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key5
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key6
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key7
import foo.starred.ionic.module.impl.general.wardrobe.keybind.impl.WardrobeKeybinds.key8
import foo.starred.kontext.abstraction.client.extension.boundKey
import foo.starred.kontext.abstraction.client.extension.client
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items

data class WardrobeKeybindSlot(
    val index: Int,
    val keybind: () -> InputConstants.Key
) {
    val hotbar by lazy {
        val index = index - 36
        client.options.keyHotbarSlots[index]
    }

    val slot: Slot?
        get() = client.player?.containerMenu?.slots?.getOrNull(index)

    val equipped: Boolean
        //~ if >= 26.2 'Items.LIME_DYE' -> 'Items.DYE.lime()'
        get() = slot?.item?.item == Items.LIME_DYE

    fun value(hotbar: Boolean): InputConstants.Key {
        return if (hotbar) this.hotbar.boundKey() else this.keybind()
    }

    companion object {
        val ALL = listOf(
            WardrobeKeybindSlot(36) { key0 },
            WardrobeKeybindSlot(37) { key1 },
            WardrobeKeybindSlot(38) { key2 },
            WardrobeKeybindSlot(39) { key3 },
            WardrobeKeybindSlot(40) { key4 },
            WardrobeKeybindSlot(41) { key5 },
            WardrobeKeybindSlot(42) { key6 },
            WardrobeKeybindSlot(43) { key7 },
            WardrobeKeybindSlot(44) { key8 }
        )
    }
}
