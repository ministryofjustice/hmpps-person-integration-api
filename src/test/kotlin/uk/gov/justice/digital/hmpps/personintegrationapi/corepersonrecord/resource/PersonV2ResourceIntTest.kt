package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PrisonerProfileSummaryResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE

class PersonV2ResourceIntTest : IntegrationTestBase() {

  private companion object {
    const val PERSON_ID = PRISONER_NUMBER
  }

  @DisplayName("GET v2/person/{personId}")
  @Nested
  inner class GetPersonById {

    @DisplayName("Security")
    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v2/person/$PERSON_ID")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v2/person/$PERSON_ID")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @DisplayName("Not Found")
    @Nested
    inner class NotFound {

      @Test
      fun `returns 404 if person not found`() {
        webTestClient.get()
          .uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND")
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_ROLE)))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @DisplayName("Happy Path")
    @Nested
    inner class HappyPath {

      @Test
      fun `can retrieve full person record`() {
        val response = webTestClient.get()
          .uri("/v2/person/$PERSON_ID")
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_ROLE)))
          .exchange()
          .expectStatus().isOk
          .expectBody(PrisonerProfileSummaryResponseDto::class.java)
          .returnResult().responseBody

        assertThat(response).isNotNull
      }
    }
  }
}
