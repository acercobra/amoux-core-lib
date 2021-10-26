import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.google.protobuf") version "0.8.17"
    `java-library`
    `maven-publish`
}

/** Artifact groupId. */
group = "org.codifysoftware.amoux"

/** Artifact version. Note that "SNAPSHOT" in the version is not supported by bintray. */
version = "1.0.2"

val myArtifactId: String = rootProject.name
val myArtifactGroup: String = project.group.toString()
val myArtifactVersion: String = project.version.toString()

/** My GitHub username. */
val myGithubUsername = "acercobra"
val token = "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"

val myDeveloperName = "James Alvarez"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("redis.clients:jedis:3.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.litote.kmongo:kmongo:4.3.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.3.0")
    implementation("io.grpc:grpc-netty-shaded:1.41.0")
    implementation("io.grpc:grpc-protobuf:1.41.0")
    implementation("io.grpc:grpc-kotlin-stub:1.1.0")
    implementation("io.grpc:grpc-stub:1.41.0")
    implementation("com.google.code.gson:gson:2.8.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/grpckt")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

protobuf {
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.17.3"
        }

        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:1.41.0"
            }

            id("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:1.1.0:jdk7@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    id("grpc")
                    id("grpckt")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("amoux-core-lib") {
            artifactId = "amoux-core-lib"
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${myGithubUsername}/${myArtifactId}")
            credentials {
                username = myGithubUsername
                password = token
            }
        }
    }
}