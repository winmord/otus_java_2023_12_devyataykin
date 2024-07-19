plugins {
    id ("org.springframework.boot")
    id ("com.google.cloud.tools.jib")
    id ("fr.brouillard.oss.gradle.jgitver")
}

dependencies {
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.telegram:telegrambots-spring-boot-starter")

    implementation("com.google.code.gson:gson")
    implementation("ch.qos.logback:logback-classic")

    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")
}

jib {
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
    from {
        image = "bellsoft/liberica-openjdk-alpine-musl:21.0.1"
    }

    to {
        image = "docker.io/winmord/film-tracker:latest"
    }
}