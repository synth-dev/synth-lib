pluginManagement {
    repositories {
        maven { setUrl("https://maven.minecraftforge.net") }
        maven { setUrl("https://maven.parchmentmc.org") }
        mavenCentral()
    }
}
rootProject.name = "synth-lib"
includeBuild("../synth-build")
