plugins {
    alias(libs.plugins.android.application)
}

// Unit tests run with this as their working directory, so tests can read the
// shipped level JSON straight off disk instead of through a mocked Context.
val assetsDir = file("src/main/assets")

android {
    namespace = "com.example.powergrid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.powergrid"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.all {
            it.workingDir = assetsDir
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.gson)

    testImplementation(libs.junit)
}