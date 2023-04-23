plugins {
    kotlin("jvm") version "1.8.20"
    application

    id("org.flywaydb.flyway") version "9.8.1"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    //Javalin dependencies
    // https://mvnrepository.com/artifact/io.javalin/javalin-bundle
    implementation("io.javalin:javalin-bundle:5.4.2")


    //jdbi dependencies
    implementation(platform("org.jdbi:jdbi3-bom:3.37.1")) //if I in the future have more than one jdbi dependency, I can use this to manage the versions (only here the version needs to be specified)
    //implementation("org.jdbi:jdbi3-core:3.38.0-rc2-SNAPSHOT") <-- version does not need to be specified
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-kotlin:3.37.1")
    // https://mvnrepository.com/artifact/org.jdbi/jdbi3-testing
    testImplementation("org.jdbi:jdbi3-testing:3.37.1")

    //flyway dependencies
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.6.0") //the driver is needed to communicate with the database
    implementation("org.flywaydb:flyway-core:9.16.3") //used in order to use flyway in my code

    //Test container dependency --> (https://www.testcontainers.org/quickstart/junit_5_quickstart/)
    testImplementation("org.testcontainers:testcontainers:1.18.0")
    testImplementation("org.testcontainers:junit-jupiter:1.18.0")
    testImplementation("org.testcontainers:postgresql:1.18.0")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

flyway {
    //TODO TGIS, probably use "System.getenv"
    url = "jdbc:postgresql://localhost:5433/       "
    user = "postgres"
    password = "Hallo123_"
    schemas = arrayOf("public")
}