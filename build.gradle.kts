plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.github.getcurrentthread"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.commonmark:commonmark:0.18.1")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
}

intellij {
    version.set("2023.1")
    updateSinceUntilBuild.set(false)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("")
        changeNotes.set("""
            <ul>
                <li>Version ${project.version}</li>
                <li>See <a href="https://github.com/getCurrentThread/recursive-markdown-generator-for-intellij/releases">GitHub Releases</a> for details.</li>
            </ul>
        """.trimIndent())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}