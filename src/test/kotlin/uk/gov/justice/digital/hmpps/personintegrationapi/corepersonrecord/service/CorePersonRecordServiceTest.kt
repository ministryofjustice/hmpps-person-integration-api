package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.DateOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class CorePersonRecordServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  lateinit var underTest: CorePersonRecordService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @BeforeEach
  fun beforeEach() {
    whenever(prisonApiClient.updateBirthPlaceForWorkingName(PRISONER_NUMBER, TEST_BIRTHPLACE_BODY)).thenReturn(ResponseEntity.noContent().build())
  }

  @Nested
  inner class UpdateCorePersonRecordField {
    @Test
    fun `can update the birthplace field`() {
      underTest.updateCorePersonRecordField(PRISONER_NUMBER, BirthplaceUpdateDto(TEST_BIRTHPLACE_VALUE))
    }

    @Test
    fun `throws an exception if the field type is not supported`() {
      assertThrows<UnknownCorePersonFieldException> {
        underTest.updateCorePersonRecordField(PRISONER_NUMBER, DateOfBirthUpdateDto(LocalDate.now()))
      }
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val TEST_BIRTHPLACE_VALUE = "London"
    val TEST_BIRTHPLACE_BODY = UpdateBirthPlace("London")
  }
}
