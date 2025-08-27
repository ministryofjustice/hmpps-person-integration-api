package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request.ReligionV1RequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.PersonProtectedCharacteristicsRoleConstants
import java.time.LocalDate

class PersonProtectedCharacteristicsV2ResourceIntTest : IntegrationTestBase() {

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/religion")
  @Nested
  inner class PutReligionByPrisonerNumberTest {
    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put()
          .uri("v2/person/$PRISONER_NUMBER/religion")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put()
          .uri("v2/person/$PRISONER_NUMBER/religion")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can update a persons religion by prisoner number`() {
        webTestClient.put()
          .uri("v2/person/$PRISONER_NUMBER/religion")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE)))
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isNoContent
      }

      @Test
      fun `can update a persons religion by prisoner number - minimal request example`() {
        webTestClient.put()
          .uri("v2/person/$PRISONER_NUMBER/religion")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE)))
          .bodyValue(MINIMAL_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.put()
          .uri("v2/person/$PRISONER_NUMBER_NOT_FOUND/religion")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE)))
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  companion object {
    val TEST_RELIGION_REQUEST_DTO =
      ReligionV1RequestDto(
        "AGNO",
        "Test Change",
        LocalDate.of(2024, 1, 1),
        false,
      )

    val MINIMAL_RELIGION_REQUEST_DTO =
      ReligionV1RequestDto(
        "AGNO",
        null,
        null,
        false,
      )

    const val TEST_DOMAIN = "RELIGION"
  }
}
