plugins {
    id("java")
}

group = "com.intellias"

val mainClass = "com.intellias.UserServiceMain"

tasks.jar {
    manifest.attributes["Main-Class"] = mainClass
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.assertj:assertj-core:3.25.3")
    implementation("org.apache.kafka:kafka-clients:3.7.0")
    implementation("org.springframework:spring-jdbc:6.1.5")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(project(":commons"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}