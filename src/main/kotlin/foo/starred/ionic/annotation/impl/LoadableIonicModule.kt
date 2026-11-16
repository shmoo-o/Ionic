package foo.starred.ionic.annotation.impl

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoadableIonicModule(val priority: Int = 0)
