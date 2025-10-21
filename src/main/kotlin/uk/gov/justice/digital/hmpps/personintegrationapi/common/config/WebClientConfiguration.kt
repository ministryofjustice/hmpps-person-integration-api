package uk.gov.justice.digital.hmpps.personintegrationapi.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.resolver.DistinguishingMarkCreateRequestResolver
import uk.gov.justice.hmpps.kotlin.auth.authorisedWebClient
import uk.gov.justice.hmpps.kotlin.auth.healthWebClient
import uk.gov.justice.hmpps.kotlin.auth.usernameAwareTokenRequestOAuth2AuthorizedClientManager
import java.time.Duration

@Configuration
class WebClientConfiguration(
  @param:Value("\${hmpps-auth.url}") private val authBaseUri: String,
  @param:Value("\${hmpps-auth.health.timeout:20s}") private val authHealthTimeout: Duration,

  @param:Value("\${prison-api.base_url}") private val prisonApiBaseUri: String,
  @param:Value("\${prison-api.health_timeout:20s}") private val prisonApiHealthTimeout: Duration,
  @param:Value("\${prison-api.timeout:30s}") private val prisonApiTimeout: Duration,

  @param:Value("\${document-api.base_url}") private val documentApiBaseUri: String,
  @param:Value("\${document-api.health_timeout:20s}") private val documentApiHealthTimeout: Duration,
  @param:Value("\${document-api.timeout:30s}") private val documentApiTimeout: Duration,
) {
  @Bean
  fun authHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(authBaseUri, authHealthTimeout)

  @Bean
  fun prisonApiHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(prisonApiBaseUri, prisonApiHealthTimeout)

  @Bean
  fun documentApiHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(documentApiBaseUri, documentApiHealthTimeout)

  @Bean
  @RequestScope
  fun prisonApiWebClient(
    clientRegistrationRepository: ClientRegistrationRepository,
    oAuth2AuthorizedClientService: OAuth2AuthorizedClientService,
    builder: WebClient.Builder,
  ) = builder.authorisedWebClient(
    usernameAwareTokenRequestOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService),
    "hmpps-person-integration-api",
    prisonApiBaseUri,
    prisonApiTimeout,
  )

  @Bean
  @DependsOn("prisonApiWebClient")
  fun prisonApiClient(prisonApiWebClient: WebClient): PrisonApiClient {
    val factory =
      HttpServiceProxyFactory.builderFor(WebClientAdapter.create(prisonApiWebClient))
        .customArgumentResolver(DistinguishingMarkCreateRequestResolver())
        .build()
    val client = factory.createClient(PrisonApiClient::class.java)

    return client
  }

  @Bean
  @DependsOn("prisonApiWebClient")
  fun referenceDataClient(prisonApiWebClient: WebClient): ReferenceDataClient {
    val factory =
      HttpServiceProxyFactory.builderFor(WebClientAdapter.create(prisonApiWebClient)).build()
    val client = factory.createClient(ReferenceDataClient::class.java)

    return client
  }

  @Bean
  @RequestScope
  fun documentApiWebClient(
    clientRegistrationRepository: ClientRegistrationRepository,
    oAuth2AuthorizedClientService: OAuth2AuthorizedClientService,
    builder: WebClient.Builder,
  ): WebClient = builder.filter(DocumentApiHeaderFilter()).authorisedWebClient(
    usernameAwareTokenRequestOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService),
    "hmpps-person-integration-api",
    documentApiBaseUri,
    documentApiTimeout,
  )

  @Bean
  @DependsOn("documentApiWebClient")
  fun documentApiClient(documentApiWebClient: WebClient): DocumentApiClient {
    val factory =
      HttpServiceProxyFactory.builderFor(WebClientAdapter.create(documentApiWebClient))
        .build()
    val client = factory.createClient(DocumentApiClient::class.java)

    return client
  }

  private class DocumentApiHeaderFilter : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
      val modifiedRequest = ClientRequest
        .from(request)
        .header("Service-Name", "hmpps-person-integration-api")
        .build()

      return next.exchange(modifiedRequest)
    }
  }
}
