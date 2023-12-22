import com.android.build.gradle.internal.res.processResources
import korlibs.korge.gradle.*

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

kotlin {
    jvmToolchain(17)
}

subprojects {
    if (this.name == "luak") {
        apply(plugin = "maven-publish")
//        tasks.withType(JavaCompile::class.java) {
//            options.release = 11
//        }
    }
}