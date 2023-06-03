import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "net.deechael"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                compileOnly("org.jetbrains:annotations:23.1.0")

                implementation("com.google.code.gson:gson:2.10.1")

                implementation("com.squareup.okhttp3:okhttp:4.11.0")
                implementation("com.squareup.retrofit2:retrofit:2.9.0")

                implementation("net.minecrell:terminalconsoleappender:1.3.0")
                implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")
                implementation("org.apache.logging.log4j:log4j-core:2.19.0")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")
                implementation("org.fusesource.jansi:jansi:2.4.0")
                implementation("org.jline:jline-terminal-jansi:3.23.0")
                implementation("net.kyori:event-api:3.0.0")
                implementation("net.kyori:event-method:3.0.0")
                implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")

                implementation("io.ktor:ktor-client-okhttp:2.0.3")
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                implementation("com.mikepenz:multiplatform-markdown-renderer:0.6.1")

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "net.deechael.kookdesktop.KookDesktopMainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kook-desktop"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<Zip>("repackageUberJar") {
    val packageUberJarForCurrentOS = tasks.getByName("packageUberJarForCurrentOS")
    dependsOn(packageUberJarForCurrentOS)
    val file = packageUberJarForCurrentOS.outputs.files.first()
    val output = File(file.parentFile, "${file.nameWithoutExtension}-repacked.jar")
    archiveFileName.set(output.absolutePath)
    destinationDirectory.set(file.parentFile.absoluteFile)
    exclude("**/Log4j2Plugins.dat")
    from(project.zipTree(file))
    doLast {
        delete(file)
        output.renameTo(file)
        logger.lifecycle("The repackaged jar is written to ${archiveFile.get().asFile.canonicalPath}")
    }
}