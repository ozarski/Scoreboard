import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ozarskiapps.scoreboard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ozarskiapps.scoreboard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    fun Packaging.() {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //modules
    globalModule()
    baseModule()
    databaseModule()
    //dependencies
    compose()
    core()
    accompanist()
    numberPicker()
    // for mutable pair
    apacheCommons()
    test()
    androidTest()
}