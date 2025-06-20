package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
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
        webTestClient.get().uri(READ_URL)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri(READ_URL)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can get addresses`() {
        webTestClient.get().uri(READ_URL)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange().expectStatus().isOk.expectBody().jsonPath("$.size()").isEqualTo(1)
          .jsonPath("$[0].addressId").isEqualTo("123")
          .jsonPath("$[0].personId").isEqualTo(PRISONER_NUMBER)
          .jsonPath("$[0].noFixedAbode").isEqualTo(true)
          .jsonPath("$[0].buildingNumber").isEqualTo(1)
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

  private companion object {
    const val READ_URL = "/v1/person/$PRISONER_NUMBER/addresses"
  }
}
