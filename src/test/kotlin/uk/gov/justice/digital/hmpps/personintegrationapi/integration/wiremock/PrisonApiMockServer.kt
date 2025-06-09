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
internal const val PRISON_API_NOT_FOUND_RESPONSE =
  // language=json
  """
  {
    "status": 404,
    "errorCode": "12345",
    "userMessage": "Prisoner not found",
    "developerMessage": "Prisoner not found"
  }
  """

internal const val PRISON_API_REFERENCE_CODES =
  // language=json
  """
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

internal const val PRISON_API_MILITARY_RECORDS =
  // language=json
  """
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

internal const val DISTINGUISHING_MARK =
  // language=json
  """
  {
    "id": 1,
    "bookingId": -1,
    "offenderNo": "A1234AA",
    "bodyPart": {
        "domain": "BODY_PART",
        "code": "LEG",
        "description": "Leg",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "markType": {
        "domain": "MARK_TYPE",
        "code": "TAT",
        "description": "Tattoo",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "side": {
        "domain": "SIDE",
        "code": "R",
        "description": "Right",
        "activeFlag": "Y",
        "listSeq": 1,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "partOrientation": {
        "domain": "PART_ORIENT",
        "code": "LOW",
        "description": "Low",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
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

internal const val DISTINGUISHING_MARKS =
  // language=json
  """
  [
    {
      "id": 1,
      "bookingId": -1,
      "offenderNo": "A1234AA",
      "bodyPart": {
        "domain": "BODY_PART",
        "code": "LEG",
        "description": "Leg",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "markType": {
        "domain": "MARK_TYPE",
        "code": "TAT",
        "description": "Tattoo",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "side": {
        "domain": "SIDE",
        "code": "R",
        "description": "Right",
        "activeFlag": "Y",
        "listSeq": 1,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "partOrientation": {
        "domain": "PART_ORIENT",
        "code": "LOW",
        "description": "Low",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
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
      "bodyPart": {
        "domain": "BODY_PART",
        "code": "ARM",
        "description": "Arm",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "markType": {
        "domain": "MARK_TYPE",
        "code": "SCAR",
        "description": "Scar",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "side": {
        "domain": "SIDE",
        "code": "L",
        "description": "Left",
        "activeFlag": "Y",
        "listSeq": 1,
        "systemDataFlag": "N",
        "subCodes": []
      },
      "partOrientation": {
        "domain": "PART_ORIENT",
        "code": "UPP",
        "description": "Upper",
        "activeFlag": "Y",
        "listSeq": 99,
        "systemDataFlag": "N",
        "subCodes": []
      },
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

internal const val IMAGE_DETAIL =
// language=json
"""
  {
    "imageId": 2461788,
    "active": false,
    "captureDate": "2008-08-27",
    "captureDateTime": "2021-07-05T10:35:17",
    "imageView": "FACE",
    "imageOrientation": "FRONT",
    "imageType": "OFF_BKG",
    "objectId": 9007199254740991
  }
"""

internal const val IMAGE_ID = "1"
internal const val IMAGE_ID_NOT_FOUND = "999"
internal val IMAGE = "image".toByteArray()

internal const val OFFENDER_ID = "12345"
internal const val OFFENDER_ID_NOT_FOUND = "54321"
internal const val ALIAS_RESPONSE =
  // language=json
  """
  {
    "prisonerNumber": "A1234AA",
    "offenderId": 12345,
    "firstName": "John",
    "middleName1": "Middleone",
    "middleName2": "Middletwo",
    "lastName": "Smith",
    "dateOfBirth": "1980-01-01",
    "nameType": {
      "domain": "NAME_TYPE",
      "code": "CN",
      "description": "Name type"
    },
    "title": {
      "domain": "TITLE",
      "code": "MR",
      "description": "Title"
    },
    "sex": {
      "domain": "SEX",
      "code": "M",
      "description": "Sex"
    },
    "ethnicity": {
      "domain": "ETHNICITY",
      "code": "W1",
      "description": "Ethnicity"
    },
    "isWorkingName": true
  }
  """

internal const val PHONE_NUMBERS =
  //language=json
  """
    [
      {
          "phoneId": 101,
          "number": "09876 543 210",
          "type": "HOME"
      },
      {
          "phoneId": 102,
          "number": "01234 567890",
          "type": "BUS",
          "extension": "111"
      }
    ]
  """

internal const val PHONE_NUMBER =
  //language=json
  """
    {
        "phoneId": 103,
        "number": "09876 543 210",
        "type": "HOME",
        "extension": "321"
    }
  """

internal const val PHONE_NUMBER_ID = 103

internal const val EMAIL_ADDRESSES =
  //language=json
  """
    [
      {
          "emailAddressId": 201,
          "email": "foo@bar.com"
      },
      {
          "emailAddressId": 202,
          "email": "bar@foo.com"
      }
    ]
  """

internal const val EMAIL_ADDRESS =
  //language=json
  """
    {
        "emailAddressId": 203,
        "email": "foo@bar.com"
    }
  """

internal const val EMAIL_ADDRESS_ID = 203

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

  fun stubUpdateSexualOrientationForWorkingName() {
    val endpoint = "sexual-orientation"
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

  fun stubUpdateDistinguishingMarkImage() {
    stubFor(
      put(urlPathMatching("/api/person/photo/$IMAGE_ID/image")).willReturn(
        aResponse().withHeader("Content-Type", "image/jpeg")
          .withStatus(HttpStatus.OK.value())
          .withBody(IMAGE),
      ),
    )
    stubFor(
      put(urlPathMatching("/api/person/photo/$IMAGE_ID_NOT_FOUND/image")).willReturn(
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

  fun stubGetAliases() {
    stubFor(
      get(urlPathMatching("/api/offenders/$PRISONER_NUMBER/aliases")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody("[${ALIAS_RESPONSE.trimIndent()}]"),
      ),
    )
    stubFor(
      get(urlPathMatching("/api/offenders/$PRISONER_NUMBER_NOT_FOUND/aliases")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody("[]"),
      ),
    )
  }

  fun stubCreateAlias() {
    stubFor(
      post(urlPathMatching("/api/offenders/$PRISONER_NUMBER/aliases")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.CREATED.value())
          .withBody(ALIAS_RESPONSE.trimIndent()),
      ),
    )
    stubFor(
      post(urlPathMatching("/api/offenders/$PRISONER_NUMBER_NOT_FOUND/aliases")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.NOT_FOUND.value())
          .withBody(PRISON_API_NOT_FOUND_RESPONSE.trimIndent()),
      ),
    )
  }

  fun stubUpdateAlias() {
    stubFor(
      put(urlPathMatching("/api/aliases/$OFFENDER_ID")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(ALIAS_RESPONSE.trimIndent()),
      ),
    )
    stubFor(
      put(urlPathMatching("/api/aliases/$OFFENDER_ID_NOT_FOUND")).willReturn(
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

  fun stubUpdatePrisonerProfileImage() {
    stubFor(
      post(urlPathMatching("/api/images/offenders/$PRISONER_NUMBER")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(IMAGE_DETAIL.trimIndent()),
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

  fun stubContactEndpoints() {
    // Phones
    // GET
    stubFor(
      get(urlPathMatching("/api/offenders/$PRISONER_NUMBER/phone-numbers")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(PHONE_NUMBERS.trimIndent()),
      ),
    )

    // POST
    stubFor(
      post(urlPathMatching("/api/offenders/$PRISONER_NUMBER/phone-numbers")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(PHONE_NUMBER.trimIndent()),
      ),
    )

    // PUT
    stubFor(
      put(urlPathMatching("/api/offenders/$PRISONER_NUMBER/phone-numbers/$PHONE_NUMBER_ID")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(PHONE_NUMBER.trimIndent()),
      ),
    )

    // Emails
    // GET
    stubFor(
      get(urlPathMatching("/api/offenders/$PRISONER_NUMBER/email-addresses")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(EMAIL_ADDRESSES.trimIndent()),
      ),
    )

    // POST
    stubFor(
      post(urlPathMatching("/api/offenders/$PRISONER_NUMBER/email-addresses")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(EMAIL_ADDRESS.trimIndent()),
      ),
    )

    // PUT
    stubFor(
      put(urlPathMatching("/api/offenders/$PRISONER_NUMBER/email-addresses/$EMAIL_ADDRESS_ID")).willReturn(
        aResponse().withHeader("Content-Type", "application/json")
          .withStatus(HttpStatus.OK.value())
          .withBody(EMAIL_ADDRESS.trimIndent()),
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
    prisonApi.stubUpdateSexualOrientationForWorkingName()

    prisonApi.stubGetMilitaryRecords()
    prisonApi.stubUpdateMilitaryRecord()
    prisonApi.stubCreateMilitaryRecord()

    prisonApi.stubGetDistinguishingMarks()
    prisonApi.stubGetDistinguishingMark()
    prisonApi.stubUpdateDistinguishingMark()
    prisonApi.stubCreateDistinguishingMark()
    prisonApi.stubGetDistinguishingMarkImage()
    prisonApi.stubUpdateDistinguishingMarkImage()
    prisonApi.stubAddDistinguishingMarkImage()

    prisonApi.stubGetAliases()
    prisonApi.stubCreateAlias()
    prisonApi.stubUpdateAlias()

    prisonApi.stubUpdatePrisonerProfileImage()
    prisonApi.stubContactEndpoints()
  }

  override fun afterAll(context: ExtensionContext): Unit = prisonApi.stop()
}
