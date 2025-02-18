package uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
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
internal const val PRISON_API_REFERENCE_CODES = """
              [
                {
                  "domain": "TEST",
                  "code": "ONE",
                  "description": "Code One",
                  "activeFlag": "Y",
                  "listSeq": 99,
                  "subCodes": []
                },
                {
                  "domain": "TEST",
                  "code": "TWO",
                  "description": "Code Two",
                  "activeFlag": "Y",
                  "listSeq": 99,
                  "subCodes": []
                }
              ]
            """
internal const val PRISON_API_MILITARY_RECORDS = """
              {
                "militaryRecords": [
                  {
                    "bookingId": -1,
                    "militarySeq": 1,
                    "warZoneCode": "WZ1",
                    "warZoneDescription": "War Zone One",
                    "startDate": "2021-01-01",
                    "endDate": "2021-12-31",
                    "militaryDischargeCode": "MD1",
                    "militaryDischargeDescription": "Military Discharge One",
                    "militaryBranchCode": "MB1",
                    "militaryBranchDescription": "Military Branch One",
                    "description": "Description One",
                    "unitNumber": "Unit Number One",
                    "enlistmentLocation": "Enlistment Location One",
                    "dischargeLocation": "Discharge Location One",
                    "selectiveServicesFlag": true,
                    "militaryRankCode": "MR1",
                    "militaryRankDescription": "Military Rank One (Army)",
                    "serviceNumber": "Service Number One",
                    "disciplinaryActionCode": "DA1",
                    "disciplinaryActionDescription": "Disciplinary Action One"
                  },
                  {
                    "bookingId": -1,
                    "militarySeq": 2,
                    "warZoneCode": "WZ2",
                    "warZoneDescription": "War Zone Two",
                    "startDate": "2022-01-01",
                    "endDate": "2022-12-31",
                    "militaryDischargeCode": "MD2",
                    "militaryDischargeDescription": "Military Discharge Two",
                    "militaryBranchCode": "MB2",
                    "militaryBranchDescription": "Military Branch Two",
                    "description": "Description Two",
                    "unitNumber": "Unit Number Two",
                    "enlistmentLocation": "Enlistment Location Two",
                    "dischargeLocation": "Discharge Location Two",
                    "selectiveServicesFlag": false,
                    "militaryRankCode": "MR2",
                    "militaryRankDescription": "Military Rank Two (Navy)",
                    "serviceNumber": "Service Number Two",
                    "disciplinaryActionCode": "DA2",
                    "disciplinaryActionDescription": "Disciplinary Action Two"
                  }
                ]
              }
            """

internal const val DISTINGUISHING_MARK = """
              {
                "id": 1,
                "bookingId": -1,
                "offenderNo": "A1234AA",
                "bodyPart": "LEG",
                "markType": "TAT",
                "side": "R",
                "partOrientation": "LOW",
                "comment": "Some comment",
                "createdAt": "2025-01-01T00:00:00",
                "createdBy": "USER",
                "photographUuids": [
                  {
                    "id": 100,
                    "latest": false
                  },
                  {
                    "id": 101,
                    "latest": true
                  }
                ]
              }
            """

internal const val DISTINGUISHING_MARKS = """
              [
                {
                  "id": 1,
                  "bookingId": -1,
                  "offenderNo": "A1234AA",
                  "bodyPart": "LEG",
                  "markType": "TAT",
                  "side": "R",
                  "partOrientation": "LOW",
                  "comment": "Some comment",
                  "createdAt": "2025-01-01T00:00:00",
                  "createdBy": "USER",
                  "photographUuids": [
                    {
                      "id": 100,
                      "latest": false
                    },
                    {
                      "id": 101,
                      "latest": true
                    }
                  ]
                },
                {
                  "id": 2,
                  "bookingId": -1,
                  "offenderNo": "A1234AA",
                  "bodyPart": "ARM",
                  "markType": "SCAR",
                  "side": "L",
                  "partOrientation": "UPP",
                  "comment": "Some comment",
                  "createdAt": "2025-01-01T00:00:00",
                  "createdBy": "USER",
                  "photographUuids": [
                    {
                      "id": 103,
                      "latest": true
                    }
                  ]
                }
              ]
            """

internal const val IMAGE_ID = "1"
internal const val IMAGE_ID_NOT_FOUND = "999"
internal val IMAGE = "image".toByteArray()

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
    stubOffenderPutEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderPutEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPutEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubUpdateBirthCountryForWorkingName() {
    val endpoint = "birth-country"
    stubOffenderPutEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderPutEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPutEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubUpdateNationalityForWorkingName() {
    val endpoint = "nationality"
    stubOffenderPutEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderPutEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPutEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubUpdateReligionForWorkingName() {
    val endpoint = "religion"
    stubOffenderPutEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderPutEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPutEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubReferenceDataCodes(domain: String = "TEST", body: String = PRISON_API_REFERENCE_CODES) {
    stubFor(
      get(urlPathMatching("/api/reference-domains/domains/$domain/all-codes")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(body),
      ),
    )
  }

  fun stubGetMilitaryRecords() {
    val endpoint = "military-records"
    stubOffenderGetEndpoint(endpoint, HttpStatus.OK, PRISONER_NUMBER, PRISON_API_MILITARY_RECORDS.trimIndent())
    stubOffenderGetEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderGetEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubUpdateMilitaryRecord() {
    val endpoint = "military-records/1"
    stubOffenderPutEndpoint(endpoint, HttpStatus.NO_CONTENT, PRISONER_NUMBER)
    stubOffenderPutEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPutEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubCreateMilitaryRecord() {
    val endpoint = "military-records"
    stubOffenderPostEndpoint(endpoint, HttpStatus.CREATED, PRISONER_NUMBER)
    stubOffenderPostEndpoint(endpoint, HttpStatus.INTERNAL_SERVER_ERROR, PRISONER_NUMBER_THROW_EXCEPTION)
    stubOffenderPostEndpoint(
      endpoint,
      HttpStatus.NOT_FOUND,
      PRISONER_NUMBER_NOT_FOUND,
      PRISON_API_NOT_FOUND_RESPONSE.trimIndent(),
    )
  }

  fun stubGetDistinguishingMarks() {
    stubFor(
      get(urlPathMatching("/api/person/$PRISONER_NUMBER/distinguishing-marks")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(DISTINGUISHING_MARKS.trimIndent()),
      ),
    )
    stubFor(
      get(urlPathMatching("/api/person/$PRISONER_NUMBER_NOT_FOUND/distinguishing-marks")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubGetDistinguishingMark() {
    stubFor(
      get(urlPathMatching("/api/person/$PRISONER_NUMBER/distinguishing-mark/1")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(DISTINGUISHING_MARK.trimIndent()),
      ),
    )
    stubFor(
      get(urlPathMatching("/api/person/$PRISONER_NUMBER_NOT_FOUND/distinguishing-mark/1")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubUpdateDistinguishingMark() {
    stubFor(
      put(urlPathMatching("/api/person/$PRISONER_NUMBER/distinguishing-mark/1")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(DISTINGUISHING_MARK.trimIndent()),
      ),
    )
    stubFor(
      put(urlPathMatching("/api/person/$PRISONER_NUMBER_NOT_FOUND/distinguishing-mark/1")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubCreateDistinguishingMark() {
    stubFor(
      post(urlPathMatching("/api/person/$PRISONER_NUMBER/distinguishing-mark")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(DISTINGUISHING_MARK.trimIndent()),
      ),
    )
    stubFor(
      post(urlPathMatching("/api/person/$PRISONER_NUMBER_NOT_FOUND/distinguishing-mark")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubGetDistinguishingMarkImage() {
    stubFor(
      get(urlPathMatching("/api/person/photo/$IMAGE_ID")).willReturn(
        aResponse().withHeader("Content-Type", "image/jpeg")
          .withStatus(HttpStatus.OK.value())
          .withBody(IMAGE),
      ),
    )
    stubFor(
      get(urlPathMatching("/api/person/photo/$IMAGE_ID_NOT_FOUND")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubAddDistinguishingMarkImage() {
    stubFor(
      post(urlPathMatching("/api/person/$PRISONER_NUMBER/distinguishing-mark/1/photo")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(DISTINGUISHING_MARK.trimIndent()),
      ),
    )
    stubFor(
      post(urlPathMatching("/api/person/$PRISONER_NUMBER_NOT_FOUND/distinguishing-mark/1/photo")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  private fun stubOffenderGetEndpoint(
    endpoint: String,
    status: HttpStatus,
    prisonerNumber: String,
    body: String? = null,
  ) {
    stubFor(
      get(urlPathMatching("/api/offenders/$prisonerNumber/$endpoint")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(status.value())
          .withBody(body),
      ),
    )
  }

  private fun stubOffenderPutEndpoint(
    endpoint: String,
    status: HttpStatus,
    prisonerNumber: String,
    body: String? = null,
  ) {
    stubFor(
      put(urlPathMatching("/api/offenders/$prisonerNumber/$endpoint")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(status.value())
          .withBody(body),
      ),
    )
  }

  private fun stubOffenderPostEndpoint(
    endpoint: String,
    status: HttpStatus,
    prisonerNumber: String,
    body: String? = null,
  ) {
    stubFor(
      post(urlPathMatching("/api/offenders/$prisonerNumber/$endpoint")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(status.value())
          .withBody(body),
      ),
    )
  }
}

class PrisonApiExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val prisonApi = PrisonApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = prisonApi.start()
  override fun beforeEach(context: ExtensionContext) {
    prisonApi.resetAll()

    prisonApi.stubReferenceDataCodes()

    prisonApi.stubUpdateBirthPlaceForWorkingName()
    prisonApi.stubUpdateBirthCountryForWorkingName()
    prisonApi.stubUpdateNationalityForWorkingName()
    prisonApi.stubUpdateReligionForWorkingName()

    prisonApi.stubGetMilitaryRecords()
    prisonApi.stubUpdateMilitaryRecord()
    prisonApi.stubCreateMilitaryRecord()

    prisonApi.stubGetDistinguishingMarks()
    prisonApi.stubGetDistinguishingMark()
    prisonApi.stubUpdateDistinguishingMark()
    prisonApi.stubCreateDistinguishingMark()
    prisonApi.stubGetDistinguishingMarkImage()
    prisonApi.stubAddDistinguishingMarkImage()
  }

  override fun afterAll(context: ExtensionContext): Unit = prisonApi.stop()
}
