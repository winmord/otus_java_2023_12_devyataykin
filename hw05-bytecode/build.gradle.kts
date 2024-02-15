import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

group = "ru.otus"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm-commons")
    implementation("ch.qos.logback:logback-classic")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    create<ShadowJar>("summatorDemoJar") {
        archiveBaseName.set("summatorDemo")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.Main",
                    "Premain-Class" to "ru.otus.changer.Agent"))
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    build {
        dependsOn("summatorDemoJar")
    }
}

tasks.test {
    useJUnitPlatform()
}