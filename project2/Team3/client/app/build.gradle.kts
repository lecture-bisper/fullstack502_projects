import java.util.Properties

//  GeoCoding 위해 properties 읽도록 import
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "bitc.full502.lostandfound"
    compileSdk = 36

    defaultConfig {
        applicationId = "bitc.full502.lostandfound"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        GeoCoding 위해 id 및 key 주입하기
        buildConfigField("String", "NCP_KEY_ID", "\"${localProps.getProperty("NCP_KEY_ID", "")}\"")
        buildConfigField("String", "NCP_KEY", "\"${localProps.getProperty("NCP_KEY", "")}\"")
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

//    buildConfig 이거 등록해놔서 프로젝트 build 위해선 true로 설정해둬야 함
    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    implementation(libs.map.sdk)
    implementation(libs.material)
    implementation(libs.material.v190)


    // GeoCoding 위한 네트워킹 연결
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // 레트로핏2
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    // 레트로핏 Gson 컨버터
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // Gson
    implementation("com.google.code.gson:gson:2.13.1")
    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-scalars
    implementation("com.squareup.retrofit2:converter-scalars:3.0.0")
    // https://mvnrepository.com/artifact/androidx.security/security-crypto
    implementation("androidx.security:security-crypto:1.1.0")
    // 웹 소켓
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
    // Firebase 라이브러리 버전 관리
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    // 필수는 아니지만 기본 분석용
    implementation("com.google.firebase:firebase-analytics")
    // FCM 토큰 발급, 메시지 수신용
    implementation("com.google.firebase:firebase-messaging")
}