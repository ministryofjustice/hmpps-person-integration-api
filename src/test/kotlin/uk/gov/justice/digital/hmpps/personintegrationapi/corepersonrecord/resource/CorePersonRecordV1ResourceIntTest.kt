package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
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
      fun `can patch core person record birthplace by prisoner number`() {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(BIRTHPLACE_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isNoContent
      }

      @Test
      fun `patch core person record birthplace accepts null value`() {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(NULL_BIRTHPLACE_PATCH_REQUEST_BODY)
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
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v1/core-person-record/profile-image")
  @Nested
  inner class PutProfileImageByPrisonerNumberTest {
    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put()
          .uri("/v1/core-person-record/profile-image?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put()
          .uri("/v1/core-person-record/profile-image?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can update core person record profile image by prisoner number`() {
        webTestClient.put()
          .uri("/v1/core-person-record/profile-image?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isNoContent
      }
    }
  }

  @DisplayName("GET v1/core-person-record/reference-data/domain/{domain}/codes")
  @Nested
  inner class GetReferenceDataCodesByDomain {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v1/core-person-record/reference-data/domain/$TEST_DOMAIN/codes")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v1/core-person-record/reference-data/domain/$TEST_DOMAIN/codes")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can update core person record profile image by prisoner number`() {
        val response =
          webTestClient.get().uri("/v1/core-person-record/reference-data/domain/$TEST_DOMAIN/codes")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ReferenceDataCodeDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(emptyList<ReferenceDataCodeDto>())
      }
    }
  }

  private companion object {

    val BIRTHPLACE_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "BIRTHPLACE",
          "value": "London"
        }
      """.trimIndent()

    val NULL_BIRTHPLACE_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "BIRTHPLACE",
          "value": null 
        }
      """.trimIndent()

    val VALID_PATCH_REQUEST_BODY = BIRTHPLACE_PATCH_REQUEST_BODY

    val MULTIPART_FILE: MultipartFile = MockMultipartFile(
      "file",
      "filename.jpg",
      MediaType.IMAGE_JPEG_VALUE,
      "I AM A JPEG, HONEST...".toByteArray(),
    )

    const val TEST_DOMAIN = "COUNTRY"

    val MULTIPART_BUILDER =
      MultipartBodyBuilder().apply {
        part("imageFile", ByteArrayResource(MULTIPART_FILE.bytes))
          .header("Content-Disposition", "form-data; name=imageFile; filename=filename.jpg")
      }
  }
}
