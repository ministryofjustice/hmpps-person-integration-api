package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.PseudonymRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class PseudonymServiceTest {
  @Mock
  private lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  private lateinit var underTest: PseudonymService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @Nested
  inner class CreatePseudonym {

    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.createAlias(PRISONER_NUMBER, ALIAS_CREATION_REQUEST))
        .thenReturn(ResponseEntity.ok(ALIAS_RESPONSE))
    }

    @Test
    fun `can create a pseudonym`() {
      val response = underTest.createPseudonym(PRISONER_NUMBER, SOURCE_SYSTEM, PSEUDONYM_REQUEST)

      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo(PSEUDONYM_RESPONSE)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.createPseudonym(PRISONER_NUMBER, "UNKNOWN_SOURCE", PSEUDONYM_REQUEST)
      }
    }

    @Test
    fun `propagates non 2xx error code`() {
      whenever(prisonApiClient.createAlias(PRISONER_NUMBER, ALIAS_CREATION_REQUEST))
        .thenReturn(ResponseEntity.badRequest().build())

      val response = underTest.createPseudonym(PRISONER_NUMBER, SOURCE_SYSTEM, PSEUDONYM_REQUEST)

      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
  }

  @Nested
  inner class UpdatePseudonym {

    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.updateAlias(OFFENDER_ID, ALIAS_UPDATE_REQUEST))
        .thenReturn(ResponseEntity.ok(ALIAS_RESPONSE))
    }

    @Test
    fun `can create a pseudonym`() {
      val response = underTest.updatePseudonym(OFFENDER_ID, SOURCE_SYSTEM, PSEUDONYM_REQUEST)

      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(PSEUDONYM_RESPONSE)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.updatePseudonym(OFFENDER_ID, "UNKNOWN_SOURCE", PSEUDONYM_REQUEST)
      }
    }

    @Test
    fun `propagates non 2xx error code`() {
      whenever(prisonApiClient.updateAlias(OFFENDER_ID, ALIAS_UPDATE_REQUEST))
        .thenReturn(ResponseEntity.badRequest().build())

      val response = underTest.updatePseudonym(OFFENDER_ID, SOURCE_SYSTEM, PSEUDONYM_REQUEST)

      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
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

    val ALIAS_CREATION_REQUEST = CreateAlias(
      firstName = FIRST_NAME,
      middleName = MIDDLE_NAME,
      lastName = LAST_NAME,
      dateOfBirth = DATE_OF_BIRTH,
      nameType = NAME_TYPE,
      title = TITLE,
      sex = SEX,
      ethnicity = ETHNICITY,
      isWorkingName = true,
    )

    val ALIAS_UPDATE_REQUEST = UpdateAlias(
      firstName = FIRST_NAME,
      middleName = MIDDLE_NAME,
      lastName = LAST_NAME,
      dateOfBirth = DATE_OF_BIRTH,
      nameType = NAME_TYPE,
      title = TITLE,
      sex = SEX,
      ethnicity = ETHNICITY,
    )

    val ALIAS_RESPONSE = CorePersonRecordAlias(
      prisonerNumber = PRISONER_NUMBER,
      offenderId = OFFENDER_ID,
      firstName = FIRST_NAME,
      middleName = MIDDLE_NAME,
      lastName = LAST_NAME,
      dateOfBirth = DATE_OF_BIRTH,
      nameType = CorePersonRecordReferenceDataValue("NAME_TYPE", NAME_TYPE, "Name type"),
      title = CorePersonRecordReferenceDataValue("TITLE", TITLE, "Title"),
      sex = CorePersonRecordReferenceDataValue("SEX", SEX, "Sex"),
      ethnicity = CorePersonRecordReferenceDataValue("ETHNICITY", ETHNICITY, "Ethnicity"),
      isWorkingName = true,
    )

    val PSEUDONYM_REQUEST = PseudonymRequestDto(
      firstName = FIRST_NAME,
      middleName = MIDDLE_NAME,
      lastName = LAST_NAME,
      dateOfBirth = DATE_OF_BIRTH,
      nameType = NAME_TYPE,
      title = TITLE,
      sex = SEX,
      ethnicity = ETHNICITY,
      isWorkingName = true,
    )

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
