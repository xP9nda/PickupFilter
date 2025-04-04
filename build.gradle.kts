import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "panda"
version = "1.1.3"
description = "An extensive item filter pickup solution featuring customisable profiles and GUIs."

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.auxilor.io/repository/maven-public/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.willfp:eco:6.75.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")

    implementation("fr.minuskube.inv:smart-invs:1.2.7") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }

    implementation("cloud.commandframework:cloud-paper:1.8.3")
    implementation("cloud.commandframework:cloud-annotations:1.8.3")

}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}


tasks.withType<ShadowJar> {
    archiveFileName.set(String.format("PickupFilter-%s.jar", version))
}