import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "org.korge.samples.mymodule"

    targetJvm()
    targetJs()
    targetIos()
    if (System.getenv("JITPACK") == "true") {
        targetAndroidDirect()
    }
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