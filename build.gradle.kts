import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "org.korge.samples.mymodule"

    targetJvm()
    targetJs()
    targetIos()
    targetAndroidDirect()
    serializationJson()
}

dependencies {
    add("commonMainApi", project(":deps"))
}

subprojects {
    if (this.name == "luak") {
        apply(plugin = "maven-publish")
    }
}