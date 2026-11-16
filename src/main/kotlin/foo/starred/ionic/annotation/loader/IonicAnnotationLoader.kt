package foo.starred.ionic.annotation.loader

import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.klassgraph.KlassGraph
import foo.starred.kontext.util.safely

object IonicAnnotationLoader {
    private val comparator = compareBy<Class<*>>({ it.getAnnotation(LoadableIonicModule::class.java)?.priority ?: 0 }, { it.name })

    fun init(pkg: String = "foo.starred.ionic") {
        KlassGraph.scan(IonicAnnotationLoader::class.java, pkg).use { result ->
            val list = result.annotated<LoadableIonicModule>().sortedWith(comparator)

            for (klass in list) {
                safely {
                    Class.forName(klass.name)
                }
            }
        }
    }
}
