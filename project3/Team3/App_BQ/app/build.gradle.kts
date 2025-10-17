import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "bitc.full502.app_bq"
    compileSdk = 36

    defaultConfig {
        applicationId = "bitc.full502.app_bq"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //    GeoCoding 위한 네트워킹 연결
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //    레트로핏2
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    //    레트로핏 Gson 컨버터
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    //    Gson
    implementation("com.google.code.gson:gson:2.13.1")
    //    Glide
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-scalars
    implementation("com.squareup.retrofit2:converter-scalars:3.0.0")
    // https://mvnrepository.com/artifact/androidx.security/security-crypto
    implementation("androidx.security:security-crypto:1.1.0")


//    QR 스캔 위한 라이브러리 사용 의존성 이하로 추가함

    // CameraX (안정판 1.5.0)
    val camerax_version = "1.5.0"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // ML Kit과 CameraX를 쉽게 붙여주는 헬퍼
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")

    // ML Kit 바코드 스캐너 (번들형: 오프라인 즉시 사용, APK ~2.4MB 증가)
    // 의존성 및 지연을 최소화하기 위해 번들형 채택함
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
}

