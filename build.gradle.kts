plugins {
    id("com.github.synth.build")
}

group = "com.github.synth" //Copied into synth info stuff
version = "1.0.0" //Also copied

synth {
    info {
        modid = "synthlib"
        displayName = "Synth-Library"
        author = "doozyz"
        group = "com.github.synth.lib"
        version = "1.0.0"
        description = """
            The main api library for all synth mods.
        """
    }
    dependencies {
        jei("10.0.0.191")
        modRuntime("mekanism:Mekanism:1.18.2-10.2.0.459:all")
        modRuntime("com.blakebr0.cucumber:Cucumber:1.18.2-5.1.0")
        modRuntime("com.blakebr0.mysticalagriculture:MysticalAgriculture:1.18.2-5.1.0")
        modImplement("software.bernie.geckolib:geckolib-1.18-forge:3.0.15")
        curseRuntime("the-one-probe", 245211, 3671753)
        curseRuntime("patchouli", 306770, 3729975)
    }


}