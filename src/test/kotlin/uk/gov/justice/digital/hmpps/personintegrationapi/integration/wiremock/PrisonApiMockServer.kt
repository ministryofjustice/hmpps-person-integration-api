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

  fun stubUpdateBirthPlaceForWorkingName(prisonerNumber: String = PRISONER_NUMBER) {
    stubFor(
      put(urlPathMatching("/api/offenders/$prisonerNumber/birth-place")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NO_CONTENT.value()),
      ),
    )
  }

  fun stubUpdateBirthPlaceForWorkingNameException(prisonerNumber: String = PRISONER_NUMBER_THROW_EXCEPTION) {
    stubFor(
      put(urlPathMatching("/api/offenders/$prisonerNumber/birth-place")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()),
      ),
    )
  }

  fun stubUpdateBirthPlaceForWorkingNameNotFound(prisonerNumber: String = PRISONER_NUMBER_NOT_FOUND) {
    stubFor(
      put(urlPathMatching("/api/offenders/$prisonerNumber/birth-place")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value()).withBody(
            PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
          ),
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
    prisonApi.stubUpdateBirthPlaceForWorkingNameException()
    prisonApi.stubUpdateBirthPlaceForWorkingNameNotFound()
  }

  override fun afterAll(context: ExtensionContext): Unit = prisonApi.stop()
}
