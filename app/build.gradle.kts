plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "me.cpele.baladr"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.jar")
            )
        )
    )

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${rootProject.extra.get("kotlin_version")}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")

    implementation("android.arch.navigation:navigation-fragment:1.0.0-alpha08")
    implementation("android.arch.navigation:navigation-ui:1.0.0-alpha08")
    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0-alpha08")
    implementation("android.arch.navigation:navigation-ui-ktx:1.0.0-alpha08")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0")
    implementation("androidx.recyclerview:recyclerview:1.0.0")

    implementation("com.google.code.gson:gson:2.8.5")

    implementation("com.github.bumptech.glide:glide:4.8.0")

    implementation("androidx.room:room-runtime:2.0.0")
    kapt("androidx.room:room-compiler:2.0.0")

    implementation("com.github.lopei:collageview:0.1.3")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0")
}
