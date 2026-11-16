@file:Suppress("PropertyName")

package foo.starred.ionic.module.impl.general.loadout.keybind.data

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key0
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key1
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key2
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key3
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key4
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key5
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key6
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key7
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key8
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key9
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key10
import foo.starred.ionic.module.impl.general.loadout.keybind.impl.LoadoutKeybinds.key11
import foo.starred.kontext.abstraction.client.extension.boundKey
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot

data class LoadoutKeybindSlot(
    val _index: Int,
    val index: Int,
    val keybind: () -> InputConstants.Key
) {
    val hotbar by lazy {
        client.options.keyHotbarSlots[_index.coerceAtMost(8)]
    }

    val slot: Slot?
        get() = client.player?.containerMenu?.slots?.getOrNull(index)

    val equipped: Boolean
        get() {
            val lore = slot?.item?.get<GenericKontextItemDataResolver.LORE, List<Component>>() ?: return false
            return lore.getOrNull(lore.lastIndex - 1)?.stripped()?.isEmpty() ?: false
        }

    fun value(hotbar: Boolean): InputConstants.Key {
        if (hotbar && index >= 41) return InputConstants.UNKNOWN
        return if (hotbar) this.hotbar.boundKey() else this.keybind()
    }

    companion object {
        val ALL = listOf(
            LoadoutKeybindSlot(0, 14) { key0 },
            LoadoutKeybindSlot(1, 15) { key1 },
            LoadoutKeybindSlot(2, 16) { key2 },
            LoadoutKeybindSlot(3, 23) { key3 },
            LoadoutKeybindSlot(4, 24) { key4 },
            LoadoutKeybindSlot(5, 25) { key5 },
            LoadoutKeybindSlot(6, 32) { key6 },
            LoadoutKeybindSlot(7, 33) { key7 },
            LoadoutKeybindSlot(8, 34) { key8 },
            LoadoutKeybindSlot(9, 41) { key9 },
            LoadoutKeybindSlot(10, 42) { key10 },
            LoadoutKeybindSlot(11, 43) { key11 }
        )
    }
}
