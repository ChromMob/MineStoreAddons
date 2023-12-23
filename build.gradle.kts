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
    compileOnly("com.github.ChromMob", "MinestoreRecode", "master-SNAPSHOT");

    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
}

var targetJavaVersion = 8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}