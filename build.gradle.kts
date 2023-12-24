import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "org.korge.samples.mymodule"

    targetJvm()
    targetJs()
    targetIos()
    if (System.getenv("JITPACK") != "true") {
        targetAndroidDirect()
    }
    serializationJson()
}

afterEvaluate {
    if (System.getenv("JITPACK") != "true") {
        tasks.getByName<AndroidLintAnalysisTask>("lintVitalAnalyzeRelease") {
            dependsOn(tasks.getByName("jvmProcessResources"))
        }
    }
}

dependencies {
    add("commonMainApi", project(":deps"))
}

subprojects {
    if (this.name == "luak") {
        apply(plugin = "maven-publish")
    }
}