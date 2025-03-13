package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.PseudonymRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.OFFENDER_ID_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER_NOT_FOUND
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISON_API_NOT_FOUND_RESPONSE
import java.time.LocalDate

class PseudonymV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("POST v1/pseudonym")
  @Nested
  inner class CreatePseudonym {

    @DisplayName("Security")
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri("/v1/pseudonym?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri("/v1/pseudonym?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @DisplayName("Not Found")
    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.post()
          .uri("/v1/pseudonym?prisonerNumber=$PRISONER_NUMBER_NOT_FOUND&sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @Nested
    @DisplayName("Validation checks")
    inner class Validation {

      @Nested
      @DisplayName("First name")
      inner class FirstName {
        @Test
        internal fun `first name must only contain valid characters`() {
          expectBadRequest(createRequest(firstName = "@@@"))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: firstName - First name is not valid")
        }

        @Test
        internal fun `first name can not be greater than 35 characters`() {
          expectBadRequest(createRequest(firstName = "A".repeat(36)))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: firstName - size must be between 0 and 35")
        }

        @Test
        internal fun `first name can not be blank`() {
          expectBadRequest(createRequest(firstName = "   "))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: firstName - must not be blank")
        }
      }

      @Nested
      @DisplayName("Last name")
      inner class LastName {
        @Test
        internal fun `last name must only contain valid characters`() {
          expectBadRequest(createRequest(lastName = "###"))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: lastName - Last name is not valid")
        }

        @Test
        internal fun `last name can not be greater than 35 characters`() {
          expectBadRequest(createRequest(lastName = "A".repeat(36)))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: lastName - size must be between 0 and 35")
        }

        @Test
        internal fun `last name can not be blank`() {
          expectBadRequest(createRequest(lastName = "   "))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: lastName - must not be blank")
        }
      }

      @Nested
      @DisplayName("Middle name")
      inner class MiddleName {

        @Test
        internal fun `first middle name must only contain valid characters`() {
          expectBadRequest(createRequest(middleName = "@@@"))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: middleName - Middle name is not valid")
        }

        @Test
        internal fun `first middle can not be greater than 35 characters`() {
          expectBadRequest(createRequest(middleName = "A".repeat(36)))
            .jsonPath("$.userMessage")
            .isEqualTo("Field: middleName - size must be between 0 and 35")
        }
      }

      private fun createRequest(
        firstName: String = "John",
        middleName: String? = "Middlename",
        lastName: String = "Smith",
        title: String? = "MR",
        dateOfBirth: LocalDate = LocalDate.parse("1990-01-02"),
        sex: String = "M",
        ethnicity: String? = "M1",
        nameType: String? = "CN",
      ) = PseudonymRequestDto(
        isWorkingName = true,
        firstName = firstName,
        middleName = middleName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        nameType = nameType,
        title = title,
        sex = sex,
        ethnicity = ethnicity,
      )

      fun expectBadRequest(body: Any): WebTestClient.BodyContentSpec = webTestClient.post()
        .uri("/v1/pseudonym?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_SYSTEM")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
        .bodyValue(body)
        .exchange()
        .expectStatus().isBadRequest
        .expectBody().jsonPath("$.status").isEqualTo(400)
    }

    @DisplayName("Happy Path")
    @Nested
    inner class HappyPath {

      @Test
      fun `can create a pseudonym`() {
        val response =
          webTestClient.post()
            .uri("/v1/pseudonym?prisonerNumber=$PRISONER_NUMBER&sourceSystem=$SOURCE_SYSTEM")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
            .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
            .exchange()
            .expectStatus().isCreated
            .expectBody(PseudonymResponseDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(PSEUDONYM_RESPONSE)
      }
    }
  }

  @DisplayName("PUT v1/pseudonym/{pseudonymId}")
  @Nested
  inner class UpdatePseudonym {

    @DisplayName("Security")
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri("/v1/pseudonym/$OFFENDER_ID?sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri("/v1/pseudonym/$OFFENDER_ID?sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @DisplayName("Not Found")
    @Nested
    inner class NotFound {

      @Test
      fun `handles a 404 not found response from downstream api`() {
        webTestClient.put().uri("/v1/pseudonym/$OFFENDER_ID_NOT_FOUND?sourceSystem=$SOURCE_SYSTEM")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
          .exchange()
          .expectStatus().isNotFound
          .expectBody().json(PRISON_API_NOT_FOUND_RESPONSE.trimIndent())
      }
    }

    @DisplayName("Happy Path")
    @Nested
    inner class HappyPath {

      @Test
      fun `can create a pseudonym`() {
        val response =
          webTestClient.put().uri("/v1/pseudonym/$OFFENDER_ID?sourceSystem=$SOURCE_SYSTEM")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(setAuthorisation(roles = listOf(CORE_PERSON_RECORD_READ_WRITE_ROLE)))
            .bodyValue(VALID_PSEUDONYM_REQUEST_BODY)
            .exchange()
            .expectStatus().isOk
            .expectBody(PseudonymResponseDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(PSEUDONYM_RESPONSE)
      }
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val OFFENDER_ID = 12345L
    const val SOURCE_SYSTEM = "NOMIS"
    const val FIRST_NAME = "John"
    const val MIDDLE_NAME = "Middlename"
    const val LAST_NAME = "Smith"
    const val NAME_TYPE = "CN"
    const val TITLE = "MR"
    const val SEX = "M"
    const val ETHNICITY = "W1"

    val DATE_OF_BIRTH = LocalDate.of(1980, 1, 1)

    val VALID_PSEUDONYM_REQUEST_BODY =
      // language=json
      """
      {
        "firstName": "John",
        "middleName": "Middlename",
        "lastName": "Smith",
        "dateOfBirth": "1980-01-01",
        "nameType": "CN",
        "title": "MR",
        "sex": "M",
        "ethnicity": "W1",
        "isWorkingName": true
      }
      """.trimIndent()

    val PSEUDONYM_RESPONSE = PseudonymResponseDto(
      prisonerNumber = PRISONER_NUMBER,
      sourceSystem = NOMIS,
      sourceSystemId = OFFENDER_ID,
      firstName = FIRST_NAME,
      middleName = MIDDLE_NAME,
      lastName = LAST_NAME,
      dateOfBirth = DATE_OF_BIRTH,
      nameType = ReferenceDataValue("NAME_TYPE_$NAME_TYPE", NAME_TYPE, "Name type"),
      title = ReferenceDataValue("TITLE_$TITLE", TITLE, "Title"),
      sex = ReferenceDataValue("SEX_$SEX", SEX, "Sex"),
      ethnicity = ReferenceDataValue("ETHNICITY_$ETHNICITY", ETHNICITY, "Ethnicity"),
      isWorkingName = true,
    )
  }
}
