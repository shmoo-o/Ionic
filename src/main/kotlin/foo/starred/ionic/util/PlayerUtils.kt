package foo.starred.ionic.util

import foo.starred.kontext.abstraction.client.extension.boundKey
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.input.impl.GenericInputState
import net.minecraft.client.KeyMapping

fun leftClick() {
    val key = client.options.keyAttack.boundKey()

    GenericInputState.press(key, true)
    KeyMapping.click(key)
    GenericInputState.press(key, false)
}

fun rightClick() {
    val key = client.options.keyUse.boundKey()

    GenericInputState.press(key, true)
    KeyMapping.click(key)
    GenericInputState.press(key, false)
}
