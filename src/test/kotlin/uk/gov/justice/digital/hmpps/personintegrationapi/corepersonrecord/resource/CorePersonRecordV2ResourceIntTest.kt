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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.CreateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.UpdateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.DUPLICATE_IDENTIFIER_RESPONSE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.EXISTING_IDENTIFIER_NOT_FOUND_RESPONSE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.EXISTING_IDENTIFIER_SEQ
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.INVALID_IDENTIFIER_RESPONSE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.INVALID_IDENTIFIER_TYPE_RESPONSE
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.NOT_FOUND_IDENTIFIER_SEQ
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.OFFENDER_ID
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.OFFENDER_ID_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE
import java.time.LocalDate

class CorePersonRecordV2ResourceIntTest : IntegrationTestBase() {

  @DisplayName("PATCH v2/person/$PRISONER_NUMBER")
  @Nested
  inner class PatchCorePersonRecordByPrisonerNumberTest {

    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.patch().uri("/v2/person/$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.patch().uri("/v2/person/$PRISONER_NUMBER")
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
        webTestClient.patch().uri("/v2/person/$PRISONER_NUMBER")
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
          .uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_PATCH_REQUEST_BODY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/profile-image")
  @Nested
  inner class PutProfileImageByPrisonerNumberTest {
    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/profile-image")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(MULTIPART_BUILDER.build()))
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/profile-image")
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
          .uri("/v2/person/$PRISONER_NUMBER/profile-image")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .body(
            BodyInserters
              .fromMultipartData(MULTIPART_BUILDER.build()),
          )
          .exchange()
          .expectStatus().isNoContent
      }

      @Test
      fun `can update core person record profile image with image source by prisoner number`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/profile-image")
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .body(
            BodyInserters
              .fromMultipartData(MULTIPART_BUILDER.build())
              .with("imageSource", "DPS_WEBCAM"),
          )
          .exchange()
          .expectStatus().isNoContent
      }
    }
  }

  @DisplayName("GET v2/person/$PRISONER_NUMBER/military-records")
  @Nested
  inner class GetMilitaryRecords {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/military-records")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/military-records")
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
          webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/military-records")
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

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/military-records")
  @Nested
  inner class UpdateMilitaryRecord {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/military-records&militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/military-records?militarySeq=1")
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
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/military-records?militarySeq=1")
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
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND/military-records?militarySeq=1")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("POST v2/person/$PRISONER_NUMBER/military-records")
  @Nested
  inner class CreateMilitaryRecord {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri("/v2/person/$PRISONER_NUMBER/military-records")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri("/v2/person/$PRISONER_NUMBER/military-records")
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
        webTestClient.post().uri("/v2/person/$PRISONER_NUMBER/military-records")
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
        webTestClient.post().uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND/military-records")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(CREATE_MILITARY_RECORD)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/nationality")
  @Nested
  inner class UpdatePrisonerNationality {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/nationality")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_NATIONALITY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/nationality")
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
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/nationality")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_NATIONALITY)
          .exchange()
          .expectStatus().isNoContent
      }

      @Test
      fun `update nationality accepts null values`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/nationality")
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
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND/nationality")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_NATIONALITY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/identifiers")
  @Nested
  inner class UpdateIdentifier {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_IDENTIFIER)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(UPDATE_IDENTIFIER)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `update existing identifier`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_IDENTIFIER)
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class Validation {
      @Test
      fun `invalid identifier value`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_IDENTIFIER_INVALID)
          .exchange()
          .expectStatus().isBadRequest
          .expectBody().json(INVALID_IDENTIFIER_RESPONSE.trimIndent())
      }

      @Test
      fun `duplicate identifier value`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_IDENTIFIER_DUPLICATE)
          .exchange()
          .expectStatus().isBadRequest
          .expectBody().json(DUPLICATE_IDENTIFIER_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `Prisoner not found`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID_NOT_FOUND&seqId=$EXISTING_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_IDENTIFIER)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }

      @Test
      fun `Existing identifier not found`() {
        webTestClient.put()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers?offenderId=$OFFENDER_ID&seqId=$NOT_FOUND_IDENTIFIER_SEQ")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_IDENTIFIER)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(EXISTING_IDENTIFIER_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("POST v2/person/$PRISONER_NUMBER/identifiers")
  @Nested
  inner class AddIdentifiers {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(ADD_IDENTIFIERS)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(ADD_IDENTIFIERS)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `Add identifiers`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(ADD_IDENTIFIERS)
          .exchange()
          .expectStatus().isCreated
      }
    }

    @Nested
    inner class Validation {
      @Test
      fun `invalid identifier value`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(ADD_IDENTIFIERS_INVALID)
          .exchange()
          .expectStatus().isBadRequest
          .expectBody().json(INVALID_IDENTIFIER_RESPONSE.trimIndent())
      }

      @Test
      fun `invalid identifier type`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(ADD_IDENTIFIERS_INVALID_TYPE)
          .exchange()
          .expectStatus().isBadRequest
          .expectBody().json(INVALID_IDENTIFIER_TYPE_RESPONSE.trimIndent())
      }

      @Test
      fun `duplicate identifier value`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(ADD_IDENTIFIERS_DUPLICATE)
          .exchange()
          .expectStatus().isBadRequest
          .expectBody().json(DUPLICATE_IDENTIFIER_RESPONSE.trimIndent())
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `Prisoner not found`() {
        webTestClient.post()
          .uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND/identifiers")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(ADD_IDENTIFIERS)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }
  }

  @DisplayName("GET v2/person/$PRISONER_NUMBER/physical-attributes")
  @Nested
  inner class GetPhysicalAttributes {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get physical attributes for prisonerNumber`() {
        val response =
          webTestClient.get().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
            .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBody(PhysicalAttributesDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(
          PhysicalAttributesDto(
            height = 180,
            weight = 75,
            hair = ReferenceDataValue("HAIR_BLK", "BLK", "Black"),
            facialHair = ReferenceDataValue("FACIAL_HAIR_MST", "MST", "Moustache"),
            face = ReferenceDataValue("FACE_OVL", "OVL", "Oval"),
            build = ReferenceDataValue("BUILD_MED", "MED", "Medium"),
            leftEyeColour = ReferenceDataValue("L_EYE_C_BRN", "BRN", "Brown"),
            rightEyeColour = ReferenceDataValue("R_EYE_C_BRN", "BRN", "Brown"),
            shoeSize = "9",
          ),
        )
      }
    }
  }

  @DisplayName("PUT v2/person/$PRISONER_NUMBER/physical-attributes")
  @Nested
  inner class UpdatePhysicalAttributes {

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(UPDATE_PHYSICAL_ATTRIBUTES)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .bodyValue(UPDATE_PHYSICAL_ATTRIBUTES)
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `update physical attributes`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER/physical-attributes")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_PHYSICAL_ATTRIBUTES)
          .exchange()
          .expectStatus().isNoContent
      }
    }

    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.put().uri("/v2/person/$PRISONER_NUMBER_NOT_FOUND/physical-attributes")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(UPDATE_PHYSICAL_ATTRIBUTES)
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

    val UPDATE_PHYSICAL_ATTRIBUTES = PhysicalAttributesRequest(
      height = 186,
      weight = 94,
      hairCode = "BRN",
      facialHairCode = "CLEAN",
      faceCode = "BULLET",
      buildCode = "FRAIL",
      leftEyeColourCode = "GRN",
      rightEyeColourCode = "BLUE",
      shoeSize = "12",
    )

    val UPDATE_IDENTIFIER = UpdateIdentifierRequestDto("6697/56U", "Some comments")
    val UPDATE_IDENTIFIER_INVALID = UpdateIdentifierRequestDto("BAD", "Some comments")
    val UPDATE_IDENTIFIER_DUPLICATE = UpdateIdentifierRequestDto("42400/52A", "Some comments")

    val ADD_IDENTIFIERS = listOf(
      CreateIdentifierRequestDto("CRO", "6697/56U", "Some comments"),
      CreateIdentifierRequestDto("PNC", "1992/0299695E", "Some more comments"),
    )
    val ADD_IDENTIFIERS_INVALID = listOf(
      CreateIdentifierRequestDto("CRO", "BAD", "Some comments"),
      CreateIdentifierRequestDto("PNC", "1992/0299695E", "Some more comments"),
    )
    val ADD_IDENTIFIERS_INVALID_TYPE = listOf(
      CreateIdentifierRequestDto("MADEUP", "6697/56U", "Some comments"),
      CreateIdentifierRequestDto("PNC", "1992/0299695E", "Some more comments"),
    )
    val ADD_IDENTIFIERS_DUPLICATE = listOf(
      CreateIdentifierRequestDto("CRO", "42400/52A", "Some comments"),
      CreateIdentifierRequestDto("PNC", "1992/0299695E", "Some more comments"),
    )
  }
}
