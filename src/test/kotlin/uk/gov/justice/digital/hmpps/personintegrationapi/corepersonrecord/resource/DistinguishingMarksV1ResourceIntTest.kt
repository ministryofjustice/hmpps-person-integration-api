package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.IMAGE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE
import java.time.LocalDateTime

class DistinguishingMarksV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("GET v1/distinguishing-marks")
  @Nested
  inner class GetDistinguishingMarks {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v1/distinguishing-marks?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v1/distinguishing-marks?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.get()
          .uri("/v1/distinguishing-marks?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND&sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get all distinguishing marks for a prisoner's latest booking`() {
        val response =
          webTestClient.get().uri("/v1/distinguishing-marks?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DistinguishingMarkDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(listOf(MARK_1, MARK_2))
      }
    }
  }

  @DisplayName("GET v1/distinguishing-mark/{markId}")
  @Nested
  inner class GetDistinguishingMarkById {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.get().uri("/v1/distinguishing-mark/$MARK_ID_NOT_FOUND?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get distinguishing mark by id`() {
        val response =
          webTestClient.get().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBody(DistinguishingMarkDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(MARK_1)
      }
    }
  }

  @DisplayName("PUT v1/distinguishing-mark/{markId}")
  @Nested
  inner class UpdateDistinguishingMark {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
          .bodyValue(DISTINGUISHING_MARK_REQUEST)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
          .bodyValue(DISTINGUISHING_MARK_REQUEST)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.put().uri("/v1/distinguishing-mark/$MARK_ID_NOT_FOUND?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(DISTINGUISHING_MARK_REQUEST)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can update a distinguishing mark`() {
        val response =
          webTestClient.put().uri("/v1/distinguishing-mark/$MARK_ID?sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
            .bodyValue(DISTINGUISHING_MARK_REQUEST)
            .exchange()
            .expectStatus().isOk
            .expectBody(DistinguishingMarkDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(MARK_1)
      }
    }
  }

  @DisplayName("POST v1/distinguishing-mark/{prisonerNumber}")
  @Nested
  inner class CreateDistinguishingMark {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri("/v1/distinguishing-mark?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_POST_BUILDER.build()))
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri("/v1/distinguishing-mark?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_POST_BUILDER.build()))
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.post()
          .uri("/v1/distinguishing-mark?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND&sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_POST_BUILDER.build()))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can add a distinguishing mark`() {
        val response =
          webTestClient.post().uri("/v1/distinguishing-mark?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
            .contentType(MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(MULTIPART_POST_BUILDER.build()))
            .exchange()
            .expectStatus().isOk
            .expectBody(DistinguishingMarkDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(MARK_1)
      }
    }
  }

  @DisplayName("GET v1/distinguishing-mark/image/{imageId}")
  @Nested
  inner class GetDistinguishingMarkImage {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v1/distinguishing-mark/image/$IMAGE_ID?sourceSystem=$SOURCE_NOMIS")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v1/distinguishing-mark/image/$IMAGE_ID?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.get().uri("/v1/distinguishing-mark/image/$IMAGE_ID_NOT_FOUND?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get distinguishing mark image`() {
        val response =
          webTestClient.get().uri("/v1/distinguishing-mark/image/$IMAGE_ID?sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBody(ByteArray::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(IMAGE)
      }
    }
  }

  @DisplayName("POST v1/distinguishing-mark/{markId}/image")
  @Nested
  inner class AddDistinguishingMarkImage {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri("/v1/distinguishing-mark/$MARK_ID/image?sourceSystem=$SOURCE_NOMIS")
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri("/v1/distinguishing-mark/$MARK_ID/image?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.post().uri("/v1/distinguishing-mark/$MARK_ID_NOT_FOUND/image?sourceSystem=$SOURCE_NOMIS")
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .contentType(MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can add distinguishing mark image`() {
        val response =
          webTestClient.post().uri("/v1/distinguishing-mark/$MARK_ID/image?sourceSystem=$SOURCE_NOMIS")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
            .contentType(MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
            .exchange()
            .expectStatus().isOk
            .expectBody(DistinguishingMarkDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(MARK_1)
      }
    }
  }

  private companion object {
    const val IMAGE_ID = "1"
    const val IMAGE_ID_NOT_FOUND = "999"
    const val MARK_ID = "A1234AA-1"
    const val MARK_ID_NOT_FOUND = "NOTFOUND-1"
    const val SOURCE_NOMIS = "NOMIS"

    val MARK_1 = DistinguishingMarkDto(
      id = 1,
      bookingId = -1,
      offenderNo = "A1234AA",
      bodyPart = "LEG",
      markType = "TAT",
      side = "R",
      partOrientation = "LOW",
      comment = "Some comment",
      createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
      createdBy = "USER",
      photographUuids = listOf(
        DistinguishingMarkImageDetail(100L, false),
        DistinguishingMarkImageDetail(101L, true),
      ),
    )

    val MARK_2 = DistinguishingMarkDto(
      id = 2,
      bookingId = -1,
      offenderNo = "A1234AA",
      bodyPart = "ARM",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Some comment",
      createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
      createdBy = "USER",
      photographUuids = listOf(
        DistinguishingMarkImageDetail(103L, true),
      ),
    )

    val DISTINGUISHING_MARK_REQUEST = DistinguishingMarkUpdateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Old wound",
    )

    val MULTIPART_FILE: MultipartFile = MockMultipartFile(
      "file",
      "filename.jpg",
      MediaType.IMAGE_JPEG_VALUE,
      "I AM A JPEG, HONEST...".toByteArray(),
    )

    val MULTIPART_BUILDER =
      MultipartBodyBuilder().apply {
        part("file", ByteArrayResource(MULTIPART_FILE.bytes))
          .header("Content-Disposition", "form-data; name=file; filename=filename.jpg")
      }

    val MULTIPART_POST_BUILDER =
      MultipartBodyBuilder().apply {
        part("file", ByteArrayResource(MULTIPART_FILE.bytes))
          .header("Content-Disposition", "form-data; name=file; filename=filename.jpg")
        part("bodyPart", "LEG")
        part("markType", "TAT")
        part("side", "R")
        part("partOrientation", "UPP")
        part("comment", "Some comment")
      }
  }
}
