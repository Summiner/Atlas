import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-velocity") version "2.3.1"
}


dependencies {
    // core
    implementation(project(":core"))

    // velocity
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    testCompileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    // annotations
    api(project(":annotations"))
    annotationProcessor(project(":annotations"))
    testAnnotationProcessor(project(":annotations"))
}

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

    runVelocity {
        velocityVersion("3.4.0-SNAPSHOT")
        pluginJars(apiJar.get().archiveFile, testPlugin.get().archiveFile)
        dependsOn(testPlugin)
        downloadPlugins {
            modrinth("luckperms", "v5.5.0-velocity")
        }
    }
}