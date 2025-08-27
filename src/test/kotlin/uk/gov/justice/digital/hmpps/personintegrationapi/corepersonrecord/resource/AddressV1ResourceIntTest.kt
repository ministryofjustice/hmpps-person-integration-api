package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodyUriSpec
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER

class AddressV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("GET v1/person/{personId}/addresses")
  @Nested
  inner class ReadAddressesByPersonIdTest {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri(ADDRESSES_URL)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri(ADDRESSES_URL)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can get addresses`() {
        webTestClient.get().uri(ADDRESSES_URL)
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange().expectStatus().isOk.expectBody().jsonPath("$.size()").isEqualTo(1)
          .jsonPath("$[0].addressId").isEqualTo("123")
          .jsonPath("$[0].personId").isEqualTo(PRISONER_NUMBER)
          .jsonPath("$[0].noFixedAbode").isEqualTo(true)
          .jsonPath("$[0].buildingNumber").isEqualTo(null)
          .jsonPath("$[0].subBuildingName").isEqualTo("1")
          .jsonPath("$[0].buildingName").isEqualTo("The Building")
          .jsonPath("$[0].thoroughfareName").isEqualTo("The Road")
          .jsonPath("$[0].dependantLocality").isEqualTo("The Locality")
          .jsonPath("$[0].postTown.code").isEqualTo("TOWN1")
          .jsonPath("$[0].postTown.description").isEqualTo("My Town")
          .jsonPath("$[0].county.code").isEqualTo("COUNTY1")
          .jsonPath("$[0].county.description").isEqualTo("My County")
          .jsonPath("$[0].country.code").isEqualTo("COUNTRY1")
          .jsonPath("$[0].country.description").isEqualTo("My Country")
          .jsonPath("$[0].postCode").isEqualTo("A1 2BC")
          .jsonPath("$[0].fromDate").isEqualTo("2021-01-02")
          .jsonPath("$[0].toDate").isEqualTo("2022-03-04")
          .jsonPath("$[0].addressTypes[0].active").isEqualTo(true)
          .jsonPath("$[0].addressTypes[0].addressUsageType.code").isEqualTo("HOME")
          .jsonPath("$[0].addressTypes[0].addressUsageType.description").isEqualTo("Home")
          .jsonPath("$[0].addressPhoneNumbers[0].contactType").isEqualTo("HOME")
          .jsonPath("$[0].addressPhoneNumbers[0].contactValue").isEqualTo("012345678")
          .jsonPath("$[0].addressPhoneNumbers[0].contactPhoneExtension").isEqualTo("567")
          .jsonPath("$[0].comment").isEqualTo("Some comment")
      }
    }
  }

  @DisplayName("POST v1/person/{personId}/addresses")
  @Nested
  inner class CreateAddressByPersonIdTest {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri(ADDRESSES_URL)
          .contentType(MediaType.APPLICATION_JSON).bodyValue(VALID_ADDRESS_REQUEST)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri(ADDRESSES_URL)
          .contentType(MediaType.APPLICATION_JSON).bodyValue(VALID_ADDRESS_REQUEST)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can create address`() {
        webTestClient.post().uri(ADDRESSES_URL)
          .contentType(MediaType.APPLICATION_JSON).bodyValue(VALID_ADDRESS_REQUEST)
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange().expectStatus().isOk.expectBody()
          .jsonPath("$.addressId").isEqualTo("123")
      }
    }

    @Nested
    inner class Validation {
      @Test
      fun `should return 400 when required fields are missing`() {
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__EMPTY)
      }

      @Test
      fun `should return 400 with parts of the address that are too long`() {
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__BUILDING_NUMBER_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__SUB_BUILDING_NAME_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__BUILDING_NAME_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__THOROUGHFARE_NAME_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__DEPENDENT_LOCALITY_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__POST_TOWN_CODE_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__COUNTY_CODE_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__COUNTRY_CODE_TOO_LONG)
        expectInvalidRequest(webTestClient.post(), ADDRESSES_URL, INVALID_ADDRESS__POST_CODE_TOO_LONG)
      }
      private fun expectInvalidRequest(requestSpec: RequestBodyUriSpec, uri: String, body: String) {
        requestSpec.uri(uri).bodyValue(body)
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .header("Content-Type", APPLICATION_JSON_VALUE).exchange().expectStatus().isBadRequest()
      }
    }
  }

  private companion object {
    const val ADDRESSES_URL = "/v1/person/$PRISONER_NUMBER/addresses"
    const val REQUIRED_FIELDS = "\"countryCode\": \"ENG\", \"fromDate\": \"2021-01-01\", \"addressTypes\": []"

    const val INVALID_ADDRESS__EMPTY = "{}"
    val INVALID_ADDRESS__BUILDING_NUMBER_TOO_LONG = "{\"buildingNumber\":\"${"1".repeat(5)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__SUB_BUILDING_NAME_TOO_LONG = "{\"subBuildingName\":\"${"A".repeat(31)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__BUILDING_NAME_TOO_LONG = "{\"buildingName\":\"${"A".repeat(51)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__THOROUGHFARE_NAME_TOO_LONG = "{\"thoroughfareName\":\"${"A".repeat(161)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__DEPENDENT_LOCALITY_TOO_LONG = "{\"dependantLocality\":\"${"A".repeat(71)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__POST_TOWN_CODE_TOO_LONG = "{\"postTownCode\":\"${"A".repeat(13)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__COUNTY_CODE_TOO_LONG = "{\"countyCode\":\"${"A".repeat(13)}\",$REQUIRED_FIELDS}"
    val INVALID_ADDRESS__COUNTRY_CODE_TOO_LONG = "{\"countryCode\":\"${"A".repeat(13)}\", \"fromDate\": \"2021-01-01\", \"addressTypes\": []}"
    val INVALID_ADDRESS__POST_CODE_TOO_LONG = "{\"postCode\":\"${"A".repeat(9)}\",$REQUIRED_FIELDS}"

    const val VALID_ADDRESS_REQUEST =
      // language=JSON
      """
      {
        "uprn": 1234,
        "noFixedAbode": true,
        "primaryAddress": true,
        "postalAddress": true,
        "buildingNumber": "1",
        "subBuildingName": "The",
        "buildingName": "Building",
        "thoroughfareName": "The Road",
        "dependantLocality": "The Locality",
        "postTownCode": "TOWN1",
        "countyCode": "COUNTY1",
        "countryCode": "COUNTRY1",
        "postCode": "A1 2BC",
        "fromDate": "2021-01-02",
        "toDate": "2022-03-04",
        "addressTypes": ["HOME"]
      }
      """
  }
}
