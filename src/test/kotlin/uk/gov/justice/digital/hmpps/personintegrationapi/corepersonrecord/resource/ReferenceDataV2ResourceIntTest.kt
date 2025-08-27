package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase

@DisplayName("GET v2/reference-data/domain/{domain}/codes")
class ReferenceDataV2ResourceIntTest : IntegrationTestBase() {

  @Nested
  inner class Security {
    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/v2/reference-data/domain/COUNTRY/codes")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `access forbidden with wrong role`() {
      webTestClient.get().uri("/v2/reference-data/domain/COUNTRY/codes")
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
        webTestClient.get().uri("/v2/reference-data/domain/$domain/codes")
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
