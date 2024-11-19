package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.common.ReligionDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request.ReligionV1RequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.response.PersonReligionInformationV1ResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.PersonProtectedCharacteristicsRoleConstants
import java.time.LocalDate

class PersonProtectedCharacteristicsV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("PUT v1/person-protected-characteristics/religion")
  @Nested
  inner class PutReligionByPrisonerNumberTest {
    @Nested
    inner class Security {

      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put()
          .uri("v1/person-protected-characteristics/religion?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put()
          .uri("v1/person-protected-characteristics/religion?prisonerNumber=$PRISONER_NUMBER")
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
        val response = webTestClient.put()
          .uri("v1/person-protected-characteristics/religion?prisonerNumber=$PRISONER_NUMBER")
          .contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE)))
          .bodyValue(TEST_RELIGION_REQUEST_DTO)
          .exchange()
          .expectStatus().isOk
          .expectBody(PersonReligionInformationV1ResponseDto::class.java)
          .returnResult().responseBody

        assertThat(response).isEqualTo(EXPECTED_RELIGION_UPDATE_RESPONSE)
      }
    }
  }

  @DisplayName("GET v1/person-protected-characteristics/reference-data/domain/{domain}/codes")
  @Nested
  inner class GetReferenceDataCodesByDomain {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get()
          .uri("/v1/person-protected-characteristics/reference-data/domain/$TEST_DOMAIN/codes")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get()
          .uri("/v1/person-protected-characteristics/reference-data/domain/$TEST_DOMAIN/codes")
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG")))
          .exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {

      @Test
      fun `can get a list of protected characteristics reference data code for a given domain`() {
        val response =
          webTestClient.get().uri("/v1/person-protected-characteristics/reference-data/domain/$TEST_DOMAIN/codes")
            .headers(setAuthorisation(roles = listOf(PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_ROLE)))
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ReferenceDataCodeDto::class.java)
            .returnResult().responseBody

        assertThat(response).isEqualTo(emptyList<ReferenceDataCodeDto>())
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

    val EXPECTED_RELIGION_UPDATE_RESPONSE = PersonReligionInformationV1ResponseDto(
      currentReligion = ReligionDto(),
      religionHistory = emptySet(),
    )

    const val TEST_DOMAIN = "RELIGION"
  }
}
