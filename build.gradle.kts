plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "me.chrommob"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven ("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("com.github.chrommob:MineStoreRecode:master-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.12.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.13.0")
    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("co.aikar:acf-bungee:0.5.1-SNAPSHOT")
    compileOnly("co.aikar:acf-velocity:0.5.1-SNAPSHOT")
    compileOnly("co.aikar:acf-sponge:0.5.1-SNAPSHOT")

    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

var targetJavaVersion = 8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    var javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    relocate("net.kyori", "me.chrommob.minestore.libs.net.kyori")
    relocate("co.aikar.acf", "me.chrommob.minestore.libs.co.aikar.acf")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}