package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.DateOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.NationalityUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class CorePersonRecordServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @Mock
  lateinit var referenceDataClient: ReferenceDataClient

  @InjectMocks
  lateinit var underTest: CorePersonRecordService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient, referenceDataClient)
  }

  @Nested
  inner class UpdateCorePersonRecordField {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.updateBirthPlaceForWorkingName(PRISONER_NUMBER, TEST_BIRTHPLACE_BODY))
        .thenReturn(ResponseEntity.noContent().build())
      whenever(prisonApiClient.updateNationalityForWorkingName(PRISONER_NUMBER, TEST_NATIONALITY_BODY))
        .thenReturn(ResponseEntity.noContent().build())
    }

    @Test
    fun `can update the birthplace field`() {
      underTest.updateCorePersonRecordField(PRISONER_NUMBER, BirthplaceUpdateDto(TEST_BIRTHPLACE_VALUE))
    }

    @Test
    fun `can update the nationality field`() {
      underTest.updateCorePersonRecordField(PRISONER_NUMBER, NationalityUpdateDto(TEST_NATIONALITY_VALUE))
    }

    @Test
    fun `throws an exception if the field type is not supported`() {
      assertThrows<UnknownCorePersonFieldException> {
        underTest.updateCorePersonRecordField(PRISONER_NUMBER, DateOfBirthUpdateDto(LocalDate.now()))
      }
    }
  }

  @Nested
  inner class ReferenceData {
    private val domain = "TEST"

    @Test
    fun `Can retrieve reference data codes`() {
      val referenceCodes = listOf(
        ReferenceDataCode(domain, "CODE1", "Code one", "Y", 1),
        ReferenceDataCode(domain, "CODE2", "Code two", "Y", 2),
        ReferenceDataCode(domain, "CODE3", "Code three", "F", 3),
      )
      val expected = listOf(
        ReferenceDataCodeDto("TEST_CODE1", "CODE1", "Code one", 1, true),
        ReferenceDataCodeDto("TEST_CODE2", "CODE2", "Code two", 2, true),
        ReferenceDataCodeDto("TEST_CODE3", "CODE3", "Code three", 3, false),
      )
      whenever(referenceDataClient.getReferenceDataByDomain(domain)).thenReturn(
        ResponseEntity.ok(referenceCodes),
      )

      val response = underTest.getReferenceDataCodes(domain)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `Propagates non-2xx status codes`(status: Int) {
      whenever(referenceDataClient.getReferenceDataByDomain(domain)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.getReferenceDataCodes(domain)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val TEST_BIRTHPLACE_VALUE = "London"
    const val TEST_NATIONALITY_VALUE = "BRIT"
    val TEST_BIRTHPLACE_BODY = UpdateBirthPlace("London")
    val TEST_NATIONALITY_BODY = UpdateNationality("BRIT")
  }
}
