import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer

repositories {
    maven("https://repo.codemc.io/repository/nms")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // -- paper api -- (base)
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

    // -- dream-platform --
    implementation("cc.dreamcode.platform:bukkit:1.13.8")
    //implementation("cc.dreamcode.platform:bukkit-hook:1.13.6")
    implementation("cc.dreamcode.platform:bukkit-config:1.13.8")
    implementation("cc.dreamcode.platform:dream-command:1.13.8")
    implementation("cc.dreamcode.platform:persistence:1.13.8")
    implementation("eu.okaeri:okaeri-persistence:3.0.1-beta.9")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.13")

    // -- dream-utilties --
    implementation("cc.dreamcode:utilities-adventure:1.5.8")
    implementation("cc.dreamcode:utilities-bukkit:1.5.8")
    // -- dream-notice --
    implementation("cc.dreamcode.notice:bukkit:1.7.4")
    implementation("cc.dreamcode.notice:bukkit-serializer:1.7.4")

    // -- dream-command --
    implementation("cc.dreamcode.command:bukkit:2.2.3")

    // -- dream-menu --
    implementation("cc.dreamcode.menu:bukkit:1.4.3")
    implementation("cc.dreamcode.menu:bukkit-serializer:1.4.3")

    // -- tasker (easy sync/async scheduler) --
    implementation("eu.okaeri:okaeri-tasker-bukkit:3.0.3-beta.2")
}

tasks.withType<ShadowJar> {

    archiveFileName.set("Dream-Bans-${project.version}.jar")
    mergeServiceFiles()

    relocate("com.cryptomorin", "cc.dreamcode.bans.libs.com.cryptomorin")
    relocate("eu.okaeri", "cc.dreamcode.bans.libs.eu.okaeri")
    relocate("net.kyori", "cc.dreamcode.bans.libs.net.kyori")

    relocate("cc.dreamcode.platform", "cc.dreamcode.bans.libs.cc.dreamcode.platform")
    relocate("cc.dreamcode.utilities", "cc.dreamcode.bans.libs.cc.dreamcode.utilities")
    relocate("cc.dreamcode.menu", "cc.dreamcode.bans.libs.cc.dreamcode.menu")
    relocate("cc.dreamcode.command", "cc.dreamcode.bans.libs.cc.dreamcode.command")
    relocate("cc.dreamcode.notice", "cc.dreamcode.bans.libs.cc.dreamcode.notice")


    transform(PropertiesFileTransformer::class.java) {
        paths.set(listOf("META-INF/native-image/org.mongodb/bson/native-image.properties"))
        mergeStrategy.set(PropertiesFileTransformer.MergeStrategy.Append)
    }
}