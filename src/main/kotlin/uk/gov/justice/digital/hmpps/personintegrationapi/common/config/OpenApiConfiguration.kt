package uk.gov.justice.digital.hmpps.personintegrationapi.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration(
  buildProperties: BuildProperties,
  @Value("\${hmpps-auth.url}") val oauthUrl: String,
) {
  private val version: String = buildProperties.version

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("http://localhost:8080").description("Local"),
        Server().url("https://person-integration-api-dev.hmpps.service.justice.gov.uk")
          .description("Development"),
        Server().url("https://person-integration-api-preprod.hmpps.service.justice.gov.uk")
          .description("Pre-Production"),
        Server().url("https://person-integration-api.hmpps.service.justice.gov.uk")
          .description("Production"),
      ),
    )
    .info(
      Info().title("Core Person Proxy Prototype").version(version)
        .contact(Contact().name("HMPPS Digital Studio").email("feedback@digital.justice.gov.uk")),
    )
    .components(
      Components().addSecuritySchemes(
        "bearer-jwt",
        SecurityScheme()
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
          .`in`(SecurityScheme.In.HEADER)
          .name("Authorization"),
      )
        .addSecuritySchemes(
          "hmpps-auth",
          SecurityScheme()
            .flows(getFlows())
            .type(SecurityScheme.Type.OAUTH2),
        ),
    )
    .addSecurityItem(SecurityRequirement().addList("bearer-jwt", listOf("read", "write")))
    .addSecurityItem(SecurityRequirement().addList("hmpps-auth"))

  fun getFlows(): OAuthFlows {
    val flows = OAuthFlows()
    val clientCredflow = OAuthFlow()
    clientCredflow.tokenUrl = "$oauthUrl/oauth/token"
    val scopes = Scopes()
      .addString("read", "Allows read of data")
      .addString("write", "Allows write of data")
    clientCredflow.scopes = scopes
    return flows.clientCredentials(clientCredflow)
  }

  @Bean
  fun v1Api(): GroupedOpenApi {
    return GroupedOpenApi.builder()
      .group("v1")
      .pathsToMatch("/v1/**")
      .build()
  }
}
