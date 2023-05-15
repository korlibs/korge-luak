pluginManagement { repositories {  mavenLocal(); mavenCentral(); google(); gradlePluginPortal()  }  }

plugins {
    //id("com.soywiz.kproject.settings") version "0.0.1-SNAPSHOT"
    id("com.soywiz.kproject.settings") version "0.2.8"
    //id("com.soywiz.kproject.settings") version "0.2.7"
}

//rootProject.name = "${rootDir.parentFile.name}-example"
rootProject.name = "${rootDir.name}-example"

kproject("./deps")
