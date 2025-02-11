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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecord
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.DateOfBirthUpdateDto
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
      whenever(prisonApiClient.updateBirthCountryForWorkingName(PRISONER_NUMBER, TEST_COUNTRY_OF_BIRTH_BODY))
        .thenReturn(ResponseEntity.noContent().build())
    }

    @Test
    fun `can update the birthplace field`() {
      underTest.updateCorePersonRecordField(PRISONER_NUMBER, BirthplaceUpdateDto(TEST_BIRTHPLACE_VALUE))
    }

    @Test
    fun `can update the country of birth field`() {
      underTest.updateCorePersonRecordField(PRISONER_NUMBER, CountryOfBirthUpdateDto(TEST_COUNTRY_OF_BIRTH_VALUE))
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
    private val parentDomain = "PARENT"

    @Test
    fun `Can retrieve reference data codes`() {
      val referenceCodes = listOf(
        ReferenceDataCode(domain, "CODE1", "Code one", "Y", 1),
        ReferenceDataCode(domain, "CODE2", "Code two", "Y", 2),
        ReferenceDataCode(domain, "CODE3", "Code three", "F", 3, "P1", parentDomain),
      )
      val expected = listOf(
        ReferenceDataCodeDto("TEST_CODE1", "CODE1", "Code one", 1, true),
        ReferenceDataCodeDto("TEST_CODE2", "CODE2", "Code two", 2, true),
        ReferenceDataCodeDto("TEST_CODE3", "CODE3", "Code three", 3, false, "P1", parentDomain),
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

  @Nested
  inner class GetMilitaryRecords {
    private val prisonerNumber = "A1234AA"
    private val militaryRecordsPrisonDto = MilitaryRecordPrisonDto(
      militaryRecords = listOf(
        MilitaryRecord(
          bookingId = -1L,
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
          militaryRankDescription = "Military Rank One (Army)",
          serviceNumber = "Service Number One",
          disciplinaryActionCode = "DA1",
          disciplinaryActionDescription = "Disciplinary Action One",
        ),
      ),
    )

    private val militaryRecords = listOf(
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
    )

    @Test
    fun `can retrieve military records`() {
      whenever(prisonApiClient.getMilitaryRecords(prisonerNumber)).thenReturn(
        ResponseEntity.ok(militaryRecordsPrisonDto),
      )

      val response = underTest.getMilitaryRecords(prisonerNumber)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(militaryRecords)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.getMilitaryRecords(prisonerNumber)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.getMilitaryRecords(prisonerNumber)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class MilitaryRecordRequest {
    private val prisonerNumber = "A1234AA"

    @Test
    fun `can create military records`() {
      whenever(prisonApiClient.createMilitaryRecord(prisonerNumber, CREATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(HttpStatus.CREATED).build(),
      )

      val response = underTest.createMilitaryRecord(prisonerNumber, CREATE_MILITARY_RECORD)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.createMilitaryRecord(prisonerNumber, CREATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.createMilitaryRecord(prisonerNumber, CREATE_MILITARY_RECORD)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdateMilitaryRecord {
    private val prisonerNumber = "A1234AA"
    private val militarySeq = 1

    @Test
    fun `can update military records`() {
      whenever(prisonApiClient.updateMilitaryRecord(prisonerNumber, militarySeq, UPDATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.noContent().build(),
      )

      val response = underTest.updateMilitaryRecord(prisonerNumber, militarySeq, UPDATE_MILITARY_RECORD)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updateMilitaryRecord(prisonerNumber, militarySeq, UPDATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updateMilitaryRecord(prisonerNumber, militarySeq, UPDATE_MILITARY_RECORD)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdateNationality {
    private val prisonerNumber = "A1234AA"

    @Test
    fun `can update the nationality field`() {
      whenever(prisonApiClient.updateNationalityForWorkingName(PRISONER_NUMBER, UPDATE_NATIONALITY))
        .thenReturn(ResponseEntity.noContent().build())

      val response = underTest.updateNationality(PRISONER_NUMBER, UPDATE_NATIONALITY)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updateNationalityForWorkingName(prisonerNumber, UPDATE_NATIONALITY)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updateNationality(prisonerNumber, UPDATE_NATIONALITY)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val TEST_BIRTHPLACE_VALUE = "London"
    const val TEST_COUNTRY_OF_BIRTH_VALUE = "ENG"
    const val TEST_OTHER_NATIONALITIES_VALUE = "French"
    val TEST_BIRTHPLACE_BODY = UpdateBirthPlace("London")
    val TEST_COUNTRY_OF_BIRTH_BODY = UpdateBirthCountry("ENG")

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
