plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.stylistiq"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stylistiq"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {

//    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.google.firebase:firebase-auth:22.1.2")

    //Firebase fireStore dependencies
    implementation("com.google.firebase:firebase-firestore:24.9.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.navigation:navigation-runtime:2.7.5")

    //Image picker dependencies
    implementation("com.github.dhaval2404:imagepicker:2.1")
    //Firebase Storage dependencies
    implementation("com.google.firebase:firebase-storage:20.3.0")

    //Tensor Flow lite dependencies
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")

    //Firebase realtime database dependencies
    implementation("com.google.firebase:firebase-database:20.3.0")

    //Loading dependencies
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //noinspection GradleCompatible,GradleCompatible
    implementation("com.android.support:palette-v7:27.1.1")
    implementation("androidx.databinding:viewbinding:8.1.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Url to imageView set dependencies
    implementation("com.github.bumptech.glide:glide:4.16.0")


//    Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics")

//    Lottie animation
    implementation("com.airbnb.android:lottie:3.7.0")

//    Button bg blur
//    implementation("com.ryanjeffreybrooks:injected-behavior:1.5")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

//    Coutry code picker
    implementation("com.hbb20:ccp:2.5.0")
}