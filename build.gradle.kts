plugins {
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // jMonkeyEngine 3.6 modulok
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-desktop:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-plugins:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-jbullet:3.6.1-stable")
}

application {
    mainClass.set("org.example.Main")
}

tasks.test {
    useJUnitPlatform()
}
