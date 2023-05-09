@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
    id("de.mannodermaus.android-junit5")
}

version = "1.0.0"
group = "com.edwardstock.android"

android {
    namespace = "com.edwardstock.inputfield"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // For Kotlin projects
    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
    packaging {
        jniLibs {
            excludes += "META-INF/LICENSE*"
        }
        resources {
            excludes += "META-INF/LICENSE*"
        }
    }

    buildFeatures {
        resValues = true
        buildConfig = false
        viewBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.8.20")
    testImplementation(kotlin("test"))
    // coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-RC")
    // junit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.3")
    // mocks
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.5")
    androidTestImplementation("io.mockk:mockk-android:1.13.5")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            groupId = project.group as String
            artifactId = "advanced-input-field"
            version = project.version as String

            pom {
                name.set(project.name)
                description.set("Android alternative for TextInputLayout with TextInputEditText")
                url.set("https://github.com/edwardstock/advanced-input-field")
                scm {
                    connection.set("scm:git:${pom.url}.git")
                    developerConnection.set(connection)
                    url.set(pom.url)
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/edwardstock/hawk/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("edwardstock")
                        name.set("Eduard Maximovich")
                        email.set("edward.vstock@gmail.com")
                        roles.add("owner")
                        timezone.set("Europe/Moscow")
                    }
                }
            }
        }
    }
    repositories {
        mavenLocal()
        maven(url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")) {
            credentials.username = findProperty("ossrhUsername") as String?
            credentials.password = findProperty("ossrhPassword") as String?
        }
    }
}

project.tasks.withType<PublishToMavenLocal> {
    dependsOn("publishAllPublicationsToMavenLocalRepository")
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
