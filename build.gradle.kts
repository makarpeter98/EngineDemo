plugins {
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }  // Minie JitPack repo
}

dependencies {
    // jMonkeyEngine 3.6 modulok
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-desktop:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.6.1-stable")

    // Minie Bullet physics
    implementation("com.github.stephengold:Minie:2.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.example.Main")
}

tasks.test {
    useJUnitPlatform()
}
