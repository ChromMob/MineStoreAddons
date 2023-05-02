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

    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.code.gson:gson:2.8.9")
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}
