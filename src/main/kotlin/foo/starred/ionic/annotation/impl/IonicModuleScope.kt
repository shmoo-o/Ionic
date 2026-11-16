package foo.starred.ionic.annotation.impl

import foo.starred.ionic.api.location.LocationAPI
import foo.starred.ionic.api.location.area.impl.PresetSkyBlockArea
import foo.starred.ionic.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.kontext.api.ALWAYS_TRUE
import foo.starred.kontext.api.data.Observable
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IonicModuleScope(
    val areas: Array<PresetSkyBlockArea> = [],
    val islands: Array<PresetSkyBlockIsland> = [],
    val skyblock: Boolean = false
) {
    companion object {
        fun get(klass: KClass<*>): Observable<Boolean> {
            val self = klass.java.getAnnotation(IonicModuleScope::class.java) ?: return ALWAYS_TRUE

            return when {
                self.areas.isNotEmpty() -> LocationAPI.area.map { it in self.areas }
                self.islands.isNotEmpty() -> LocationAPI.island.map { it in self.islands }
                self.skyblock -> LocationAPI.skyblock
                else -> ALWAYS_TRUE
            }
        }
    }
}
