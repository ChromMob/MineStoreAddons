plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "me.chrommob"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("me.chrommob:MineStore:0.1")
    compileOnly("net.kyori:adventure-api:4.12.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.code.gson:gson:2.8.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    relocate("net.kyori", "me.chrommob.minestore.libs.net.kyori")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}