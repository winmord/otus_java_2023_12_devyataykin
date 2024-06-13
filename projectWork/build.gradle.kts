dependencies {
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
//    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")

    implementation("com.google.code.gson:gson")
    implementation("ch.qos.logback:logback-classic")

//    implementation("org.flywaydb:flyway-core")
//    implementation("org.postgresql:postgresql")
//    implementation("com.google.code.findbugs:jsr305")
}