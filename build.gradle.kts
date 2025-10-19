plugins {
    `java-library`
    id("idea")
    id("com.gradleup.shadow") version "9.2.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18" apply false
}

idea {
    project.jdkName = "23"
}

allprojects {
    group = "cc.dreamcode.bans"
    version = "1.0.0-beta.2"

    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenCentral()
        maven("https://repo.dreamcode.cc/releases")
        maven("https://storehouse.okaeri.eu/repository/maven-public")
    }
}

subprojects {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    dependencies {
        /* General */
        val lombok = "1.18.38"
        compileOnly("org.projectlombok:lombok:$lombok")
        annotationProcessor("org.projectlombok:lombok:$lombok")
        testCompileOnly("org.projectlombok:lombok:$lombok")
        testAnnotationProcessor("org.projectlombok:lombok:$lombok")
    }
}

tasks.register("pluginVersion") {
    println(project.version)
}