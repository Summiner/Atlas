plugins {
    id("java-library")
    id("maven-publish")
}

allprojects {
    apply {
        plugin("java-library")
        plugin("maven-publish")
    }

    group = "rs.jamie"
    version = "1.1.0"

    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = uri("https://libraries.minecraft.net")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        implementation("com.mojang:brigadier:1.0.18")
        implementation("io.github.classgraph:classgraph:4.8.162")
        implementation("net.kyori:adventure-api:4.25.0")
    }

}

subprojects {
    if (name == "paper" || name == "velocity") {

        val sourcesJar by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        publishing {
            publications {
                create<MavenPublication>("atlas") {
                    from(components["java"])
                    groupId = "rs.jamie"
                    artifactId = "atlas-${project.name}"
                    version = project.version.toString()
                    artifact(sourcesJar.get())
                }
            }
        }
    }
}