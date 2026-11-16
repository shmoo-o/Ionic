@file:Suppress("ObjectPropertyName")

package foo.starred.ionic.module.impl.render

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.module.base.Module

@LoadableIonicModule
object CameraTweaks : Module(
    "Camera tweaks",
    "Render tweaks for camera",
    ConfigCategory.RENDER
) {
    private val _front by config.switch("No front camera", true)
    private val _clip by config.switch("Camera clip")
    private val _dist by config.switch("Custom distance")

    @JvmStatic
    val `distance$blocks` by config.slider("Distance", 4f, 3f, 15f, "blocks", true)

    @JvmStatic
    val front: Boolean
        get() = enabled && _front

    @JvmStatic
    val `distance$boolean`: Boolean
        get() = enabled && _dist

    @JvmStatic
    val clip: Boolean
        get() = enabled && _clip
}
