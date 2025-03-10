package uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

private const val VIRUS_SCAN_PASSED = """
              {
                "status": "PASSED",
                "result": "Success"
              }
            """

private const val VIRUS_SCAN_FAILED = """
              {
                "status": "FAILED",
                "result": "Failure - Found virus ABC_123"
              }
            """

private const val VIRUS_SCAN_ERROR = """
              {
                "status": "ERROR",
                "result": "Error - some error occurred"
              }
            """

internal const val VIRUS_SCAN_FAILED_RESPONSE = """
              {
                "status": 400,
                "userMessage": "Virus scan failed - Failure - Found virus ABC_123",
                "developerMessage": "Virus scan failed - Failure - Found virus ABC_123"
              }
            """

internal const val VIRUS_SCAN_ERROR_RESPONSE = """
              {
                "status": 400,
                "userMessage": "Virus scan error - Error - some error occurred",
                "developerMessage": "Virus scan error - Error - some error occurred"
              }
            """

internal const val VIRUS_SCAN_UNEXPECTED_ERROR_RESPONSE = """
              {
                "status": 500,
                "userMessage": "An unexpected error occurred during virus scanning",
                "developerMessage": "An unexpected error occurred during virus scanning"
              }
            """

class DocumentApiMockServer : WireMockServer(8083) {
  fun stubHealthPing(status: Int) {
    stubFor(
      get("/health/ping").willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withBody("""{"status":"${if (status == 200) "UP" else "DOWN"}"}""").withStatus(status),
      ),
    )
  }

  fun stubVirusScanPassed() {
    stubFor(
      post(urlPathMatching("/documents/scan"))
        .withHeader("Service-Name", EqualToPattern("hmpps-person-integration-api"))
        .withMultipartRequestBody(MultipartValuePatternBuilder().withFileName("filename.jpg"))
        .willReturn(
          aResponse().withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody(VIRUS_SCAN_PASSED),
        ),
    )
  }

  fun stubVirusScanFailed() {
    stubFor(
      post(urlPathMatching("/documents/scan"))
        .withHeader("Service-Name", EqualToPattern("hmpps-person-integration-api"))
        .withMultipartRequestBody(MultipartValuePatternBuilder().withFileName("virus.jpg"))
        .willReturn(
          aResponse().withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody(VIRUS_SCAN_FAILED),
        ),
    )
  }

  fun stubVirusScanError() {
    stubFor(
      post(urlPathMatching("/documents/scan"))
        .withHeader("Service-Name", EqualToPattern("hmpps-person-integration-api"))
        .withMultipartRequestBody(MultipartValuePatternBuilder().withFileName("error.jpg"))
        .willReturn(
          aResponse().withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody(VIRUS_SCAN_ERROR),
        ),
    )
  }

  fun stubVirusScanUnexpectedError() {
    stubFor(
      post(urlPathMatching("/documents/scan"))
        .withHeader("Service-Name", EqualToPattern("hmpps-person-integration-api"))
        .withMultipartRequestBody(MultipartValuePatternBuilder().withFileName("unexpected_error.jpg"))
        .willReturn(
          aResponse().withHeader("Content-Type", "application/json")
            .withStatus(500),
        ),
    )
  }
}

class DocumentApiExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val documentApi = DocumentApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = documentApi.start()
  override fun beforeEach(context: ExtensionContext) {
    documentApi.resetAll()
    documentApi.stubVirusScanPassed()
    documentApi.stubVirusScanFailed()
    documentApi.stubVirusScanError()
    documentApi.stubVirusScanUnexpectedError()
  }

  override fun afterAll(context: ExtensionContext): Unit = documentApi.stop()
}
