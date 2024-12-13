package uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.http.HttpStatus

internal const val PRISONER_NUMBER = "A1234AA"
internal const val PRISONER_NUMBER_THROW_EXCEPTION = "THROW"
internal const val PRISONER_NUMBER_NOT_FOUND = "NOTFOUND"
internal const val PRISON_API_NOT_FOUND_RESPONSE = """
              {
                "status": 404,
                "errorCode": "12345",
                "userMessage": "Prisoner not found",
                "developerMessage": "Prisoner not found"
              }
            """

class PrisonApiMockServer : WireMockServer(8082) {
  fun stubHealthPing(status: Int) {
    stubFor(
      get("/health/ping").willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withBody("""{"status":"${if (status == 200) "UP" else "DOWN"}"}""").withStatus(status),
      ),
    )
  }

  fun stubUpdateBirthPlaceForWorkingName() {
    val endpoint = "birth-place"
    stubOffenderEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubUpdateNationalityForWorkingName() {
    val endpoint = "nationality"
    stubOffenderEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  private fun stubOffenderEndpoint(endpoint: String, status: HttpStatus, prisonerNumber: String, body: String? = null) {
    stubFor(
      put(urlPathMatching("/api/offenders/$prisonerNumber/$endpoint")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(status.value())
          .withBody(body),
      ),
    )
  }
}

class PrisonApiExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {
  companion object {
    @JvmField
    val prisonApi = PrisonApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = prisonApi.start()
  override fun beforeEach(context: ExtensionContext) {
    prisonApi.resetAll()
    prisonApi.stubUpdateBirthPlaceForWorkingName()
    prisonApi.stubUpdateNationalityForWorkingName()
  }

  override fun afterAll(context: ExtensionContext): Unit = prisonApi.stop()
}
