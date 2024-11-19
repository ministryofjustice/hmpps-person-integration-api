package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE

class CorePersonRecordV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("PATCH v1/core-person-record")
  @Nested
  inner class PatchCorePersonRecordByPrisonerNumberTest {

    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can patch core person record by prisoner number`() {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE)))
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.patch()
          .uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE)))
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v1/core-person-record/profile-image")
  @Nested
  inner class PutProfileImageByPrisonerNumberTest

  @DisplayName("GET v1/core-person-record/reference-data/domain/{domain}/codes")
  @Nested
  inner class GetReferenceDataCodesByDomain

  private companion object {

    val VALID_PATCH_REQUEST_BODY =
      // language=json
      """
        { 
          "fieldName": "BIRTHPLACE",
          "fieldValue": "London"
        }
      """.trimIndent()
  }
}
