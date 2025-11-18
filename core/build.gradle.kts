import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}


dependencies {
    implementation("io.github.classgraph:classgraph:4.8.162")
    implementation("com.mojang:brigadier:1.0.18")
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")
    testCompileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")
    api(project(":annotations"))

    annotationProcessor(project(":annotations"))
    testImplementation(platform("org.junit:junit-bom:5.13.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor(project(":annotations"))
}

publishing {
    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    publications {
        create<MavenPublication>("atlas") {
            from(components["java"])
            groupId = "rs.jamie"
            artifactId = "atlas"
            version = project.version.toString()
            artifact(sourcesJar.get())
        }
    }
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

    runServer {
        minecraftVersion("1.21.5")
//        jvmArgs = listOf(
//            "-Dminecraft.api.auth.host=https://authserver.mojang.com/",
//            "-Dminecraft.api.account.host=https://api.mojang.com/",
//            "-Dminecraft.api.services.host=https://api.minecraftservices.com/",
//            "-Dminecraft.api.profiles.host=https://api.mojang.com/",
//            "-Dminecraft.api.session.host=http://127.0.0.1:3025",
//            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5025"
//        )
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