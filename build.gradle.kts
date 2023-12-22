import korlibs.korge.gradle.*
import korlibs.modules.publishing

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "org.korge.samples.mymodule"

    targetJvm()
    targetJs()
    targetIos()
    serializationJson()
}

dependencies {
    add("commonMainApi", project(":deps"))
}

//kotlin {
//    jvmToolchain(17)
//}

subprojects {
    if (this.name == "luak") {
        apply(plugin = "maven-publish")
    }
}