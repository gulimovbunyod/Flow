import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// ---------------------------------------------------------------------------
// IMZO: debug va release IKKALASI bir xil kalit bilan imzolanadi.
// Qiymatlar: CI'da muhit o'zgaruvchilaridan, lokalda keystore.properties'dan.
// ---------------------------------------------------------------------------
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun signingValue(envName: String, propName: String): String? =
    System.getenv(envName)?.takeIf { it.isNotBlank() }
        ?: keystoreProps.getProperty(propName)?.takeIf { it.isNotBlank() }

val signStoreFile: String? = signingValue("KEYSTORE_FILE", "storeFile")
val signStorePassword: String? = signingValue("KEYSTORE_PASSWORD", "storePassword")
val signKeyAlias: String? = signingValue("KEY_ALIAS", "keyAlias")
val signKeyPassword: String? = signingValue("KEY_PASSWORD", "keyPassword")

val hasSigning: Boolean =
    signStorePassword != null && signKeyAlias != null && signKeyPassword != null &&
        signStoreFile?.let { rootProject.file(it).exists() } == true

// versionCode har build'da oshadi (GITHUB_RUN_NUMBER). versionName qo'lda.
val versionCodeBase = (findProperty("flow.versionCodeBase") as String?)?.toIntOrNull() ?: 0
val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

android {
    namespace = "com.gulimovbunyod.flow"
    compileSdk = 36

    defaultConfig {
        // HECH QACHON o'zgarmaydi. Debug uchun applicationIdSuffix YO'Q.
        applicationId = "com.gulimovbunyod.flow"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCodeBase + runNumber
        versionName = "0.1.0"
    }

    signingConfigs {
        if (hasSigning) {
            create("flow") {
                storeFile = rootProject.file(signStoreFile!!)
                storePassword = signStorePassword
                keyAlias = signKeyAlias
                keyPassword = signKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            if (hasSigning) signingConfig = signingConfigs.getByName("flow")
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            if (hasSigning) signingConfig = signingConfigs.getByName("flow")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
}
