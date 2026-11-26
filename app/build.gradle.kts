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

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            // Backend local visto desde el emulador (10.0.2.2 = localhost del PC).
            // Si no se aprueban las rutas unificadas (P11), cambiar a "http://10.0.2.2:8080/".
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
            // Temporal: permite probar la navegación sin backend (login/registro de prueba).
            // Pasar a false cuando estén listos /auth y JWT (T-10).
            buildConfigField("boolean", "MODO_SIN_BACKEND", "true")
        }
        release {
            // TODO: reemplazar por la URL del servidor desplegado (T-13).
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
            buildConfigField("boolean", "MODO_SIN_BACKEND", "false")
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
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.viewpager2)
    implementation(libs.swiperefreshlayout)
    // Red (Retrofit + Gson + logs de OkHttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    // Ubicación y mapas (HU-06, HU-09, HU-10, HU-11)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    // Cámara (HU-06, HU-17)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    // Room no se incluye en este entregable (T-06 pasó al backlog).
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}