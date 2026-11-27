plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "co.edu.uniminuto.Movil_EcoBogota"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "co.edu.uniminuto.Movil_EcoBogota"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    constraints {
        implementation(libs.androidx.viewpager2) {
            because("Evitar la versión beta que introduce recyclerview 1.4.0")
        }
    }
}