plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.3.1"
  kotlin("plugin.spring") version "2.3.21"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencyCheck {
  suppressionFiles.add("azure-dependency-check-suppress.xml")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.4.0")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
  implementation("io.sentry:sentry-spring-boot-4:8.42.0")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.4.0")
  testImplementation("org.springframework.boot:spring-boot-starter-webclient-test")
  testImplementation("org.springframework.boot:spring-boot-webtestclient")
  testImplementation("org.wiremock:wiremock-standalone:3.13.2")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.42") {
    exclude(group = "io.swagger.core.v3")
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
      jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24
      freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
  }
}
