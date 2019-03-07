// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by rootProject.extra { "1.3.11" }
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0-beta05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://dl.bintray.com/musichin/maven") }
    }
}
