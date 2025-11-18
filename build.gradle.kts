plugins {
    id("java-library")
}

allprojects {
    apply {
        plugin("java-library")
    }

    group = "rs.jamie"
    version = "1.0.0"

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

}