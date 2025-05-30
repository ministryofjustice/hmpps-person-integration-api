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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE
import java.time.LocalDate

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
        expectSuccessfulRequestWith(BIRTHPLACE_PATCH_REQUEST_BODY)
      }

      @Test
      fun `patch core person record birthplace accepts null value`() {
        expectSuccessfulRequestWith(NULL_BIRTHPLACE_PATCH_REQUEST_BODY)
      }

      @Test
      fun `can patch core person record country of birth by prisoner number`() {
        expectSuccessfulRequestWith(COUNTRY_OF_BIRTH_PATCH_REQUEST_BODY)
      }

      @Test
      fun `patch core person record country of birth accepts null value`() {
        expectSuccessfulRequestWith(NULL_COUNTRY_OF_BIRTH_PATCH_REQUEST_BODY)
      }

      @Test
      fun `can patch core person record sexual orientation by prisoner number`() {
        expectSuccessfulRequestWith(SEXUAL_ORIENTATION_PATCH_REQUEST_BODY)
      }

      @Test
      fun `patch core person record sexual orientation accepts null value`() {
        expectSuccessfulRequestWith(NULL_SEXUAL_ORIENTATION_PATCH_REQUEST_BODY)
      }

      private fun expectSuccessfulRequestWith(body: Any) {
        webTestClient.patch().uri("/v1/core-person-record?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(body)
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
      fun `can get reference data codes by domain`() {
        val domain = "TEST"
        val response =
          webTestClient.get().uri("/v1/core-person-record/reference-data/domain/$domain/codes")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ReferenceDataCodeDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(
          listOf(
            ReferenceDataCodeDto("TEST_ONE", "ONE", "Code One", 99, true),
            ReferenceDataCodeDto("TEST_TWO", "TWO", "Code Two", 99, true),
          ),
        )
      }
    }
  }

  @DisplayName("GET v1/core-person-record/military-records")
  @Nested
  inner class GetMilitaryRecords {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get military records for prisonerNumber`() {
        val response =
          webTestClient.get().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(MilitaryRecordDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(
          listOf(
            MilitaryRecordDto(
              militarySeq = 1,
              warZoneCode = "WZ1",
              warZoneDescription = "War Zone One",
              startDate = LocalDate.parse("2021-01-01"),
              endDate = LocalDate.parse("2021-12-31"),
              militaryDischargeCode = "MD1",
              militaryDischargeDescription = "Military Discharge One",
              militaryBranchCode = "MB1",
              militaryBranchDescription = "Military Branch One",
              description = "Description One",
              unitNumber = "Unit Number One",
              enlistmentLocation = "Enlistment Location One",
              dischargeLocation = "Discharge Location One",
              selectiveServicesFlag = true,
              militaryRankCode = "MR1",
              militaryRankDescription = "Military Rank One",
              serviceNumber = "Service Number One",
              disciplinaryActionCode = "DA1",
              disciplinaryActionDescription = "Disciplinary Action One",
            ),
            MilitaryRecordDto(
              militarySeq = 2,
              warZoneCode = "WZ2",
              warZoneDescription = "War Zone Two",
              startDate = LocalDate.parse("2022-01-01"),
              endDate = LocalDate.parse("2022-12-31"),
              militaryDischargeCode = "MD2",
              militaryDischargeDescription = "Military Discharge Two",
              militaryBranchCode = "MB2",
              militaryBranchDescription = "Military Branch Two",
              description = "Description Two",
              unitNumber = "Unit Number Two",
              enlistmentLocation = "Enlistment Location Two",
              dischargeLocation = "Discharge Location Two",
              selectiveServicesFlag = false,
              militaryRankCode = "MR2",
              militaryRankDescription = "Military Rank Two",
              serviceNumber = "Service Number Two",
              disciplinaryActionCode = "DA2",
              disciplinaryActionDescription = "Disciplinary Action Two",
            ),
          ),
        )
      }
    }
  }

  @DisplayName("PUT v1/core-person-record/military-records")
  @Nested
  inner class UpdateMilitaryRecord {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER&militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER&militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `update military record`() {
        webTestClient.put().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER&militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.put().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND&militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("POST v1/core-person-record/military-records")
  @Nested
  inner class CreateMilitaryRecord {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `create military record`() {
        webTestClient.post().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isCreated
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.post().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v1/core-person-record/nationality")
  @Nested
  inner class UpdatePrisonerNationality {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v1/core-person-record/nationality?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_NATIONALITY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v1/core-person-record/nationality?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(UPDATE_NATIONALITY)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `update nationality`() {
        webTestClient.put().uri("/v1/core-person-record/nationality?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNoContent
      }

      @Test
      fun `update nationality accepts null values`() {
        webTestClient.put().uri("/v1/core-person-record/nationality?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UpdateNationality(null, null))
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.post().uri("/v1/core-person-record/military-records?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
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

    val COUNTRY_OF_BIRTH_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "COUNTRY_OF_BIRTH",
          "value": "London"
        }
      """.trimIndent()

    val NULL_COUNTRY_OF_BIRTH_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "COUNTRY_OF_BIRTH",
          "value": null
        }
      """.trimIndent()

    val SEXUAL_ORIENTATION_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "SEXUAL_ORIENTATION",
          "value": "HET"
        }
      """.trimIndent()

    val NULL_SEXUAL_ORIENTATION_PATCH_REQUEST_BODY =
      // language=json
      """
        {
          "fieldName": "SEXUAL_ORIENTATION",
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

    val UPDATE_MILITARY_RECORD = MilitaryRecordRequest(
      warZoneCode = "AFG",
      startDate = LocalDate.parse("2021-01-01"),
      militaryDischargeCode = "HON",
      militaryBranchCode = "ARM",
      description = "Description One",
      unitNumber = "Unit Number One",
      enlistmentLocation = "Enlistment Location One",
      dischargeLocation = "Discharge Location One",
      selectiveServicesFlag = false,
      militaryRankCode = "CPL_ARM",
      serviceNumber = "Service Number One",
      disciplinaryActionCode = "CM",
    )

    val CREATE_MILITARY_RECORD = MilitaryRecordRequest(
      startDate = LocalDate.parse("2021-01-01"),
      militaryBranchCode = "NAV",
      selectiveServicesFlag = false,
    )

    val UPDATE_NATIONALITY = UpdateNationality("BRIT", "French")
  }
}
