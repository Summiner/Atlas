import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}


dependencies {
    // core
    implementation(project(":core"))

    // paper
    compileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")
    testCompileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")

    // annotations
    api(project(":annotations"))
    annotationProcessor(project(":annotations"))
    testAnnotationProcessor(project(":annotations"))

    // junit
    testImplementation(platform("org.junit:junit-bom:5.13.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Hot Swapping
tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    jvmArgs("-XX:+HotSwap")
}


tasks {
    val apiJar = named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.getByName("runtimeClasspath"))
    }

    val testPlugin = named<ShadowJar>("shadowJar") {
        archiveBaseName.set("atlas-test")
        from(sourceSets.test.get().output)
        archiveClassifier.set("test")
        manifest {
            attributes("Main-Class" to "rs.jamie.atlas.AtlasTest")
        }
    }


    build {
        dependsOn(apiJar)
    }

    runServer {
        minecraftVersion("1.21.5")
        pluginJars(apiJar.get().archiveFile, testPlugin.get().archiveFile)
        dependsOn(testPlugin)
        downloadPlugins {
            modrinth("viabackwards", "5.4.2")
            modrinth("viaversion", "5.4.2")
            modrinth("luckperms", "v5.5.0-bukkit")
            modrinth("placeholderapi", "2.11.6")
        }
    }
}