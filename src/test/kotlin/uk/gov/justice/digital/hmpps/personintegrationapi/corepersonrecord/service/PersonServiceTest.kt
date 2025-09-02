package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.EmailAddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.FullPersonPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecord
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhoneNumberPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhysicalAttributesPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataValuePrisonDto
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PersonServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  lateinit var underTest: PersonService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @Nested
  inner class GetPerson {

    @BeforeEach
    fun setup() {
      whenever(prisonApiClient.getFullPerson(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(FULL_PERSON_PRISON_DTO))
    }

    @Test
    fun `returns status code if response body is null`() {
      whenever(prisonApiClient.getFullPerson(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build())

      val response = underTest.getPerson(PRISONER_NUMBER)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
      assertThat(response.body).isNull()
    }

    @Test
    fun `can retrieve full person details`() {
      val response = underTest.getPerson(PRISONER_NUMBER)

      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      val body = response.body!!

      // Full assertion covered in other tests already.
      assertThat(body.addresses).hasSize(1)
      assertThat(body.pseudonyms).hasSize(1)
      assertThat(body.contacts).hasSize(2)
      assertThat(body.militaryRecords).hasSize(1)
      assertThat(body.physicalAttributes).isNotNull
      assertThat(body.distinguishingMarks).hasSize(1)
    }

    @Test
    fun `handles empty collections gracefully when some data is unavailable`() {
      val dtoWithEmptyCollections = FullPersonPrisonDto(
        addresses = emptyList(),
        aliases = emptyList(),
        phones = emptyList(),
        emails = emptyList(),
        militaryRecord = MilitaryRecordPrisonDto(militaryRecords = emptyList()),
        physicalAttributes = PHYSICAL_ATTRIBUTES_PRISON_DTO,
        distinguishingMarks = emptyList(),
      )

      whenever(prisonApiClient.getFullPerson(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(dtoWithEmptyCollections))

      val response = underTest.getPerson(PRISONER_NUMBER)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      val body = response.body!!

      assertThat(body.addresses).isEmpty()
      assertThat(body.pseudonyms).isEmpty()
      assertThat(body.contacts).isEmpty()
      assertThat(body.militaryRecords).isEmpty()
      assertThat(body.physicalAttributes).isNotNull
      assertThat(body.distinguishingMarks).isEmpty()
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"

    val ADDRESS_PRISON_DTO = AddressPrisonDto(
      addressId = 1L,
      noFixedAddress = false,
      flat = "Flat 1",
      premise = "Building",
      street = "Street",
      locality = "Locality",
      town = "Town",
      townCode = "TWN",
      county = "County",
      countyCode = "CNT",
      country = "Country",
      countryCode = "CTR",
      postalCode = "PC1 1PC",
      startDate = LocalDate.now(),
      endDate = LocalDate.now().plusYears(5),
      addressUsages = listOf(),
      primary = true,
      mail = false,
      comment = "Comment",
      phones = listOf(),
      addressType = "AT",
    )

    val ALIAS = CorePersonRecordAlias(
      prisonerNumber = PRISONER_NUMBER,
      offenderId = 123L,
      firstName = "John",
      middleName1 = "M1",
      middleName2 = "M2",
      lastName = "Smith",
      dateOfBirth = LocalDate.of(1990, 1, 1),
      nameType = CorePersonRecordReferenceDataValue("NAME_TYPE", "CN", "Name type"),
      title = CorePersonRecordReferenceDataValue("TITLE", "MR", "Title"),
      sex = CorePersonRecordReferenceDataValue("SEX", "M", "Sex"),
      ethnicity = CorePersonRecordReferenceDataValue("ETHNICITY", "W1", "Ethnicity"),
      isWorkingName = true,
    )

    val MILITARY_RECORD_PRISON_DTO = MilitaryRecordPrisonDto(
      militaryRecords = listOf(
        MilitaryRecord(
          bookingId = -1L,
          militarySeq = 1,
          warZoneCode = "WZ1",
          warZoneDescription = "War Zone One",
          startDate = LocalDate.of(2021, 1, 1),
          endDate = LocalDate.of(2021, 12, 31),
          militaryDischargeCode = "MD1",
          militaryDischargeDescription = "Discharge",
          militaryBranchCode = "MB1",
          militaryBranchDescription = "Branch",
          description = "Desc",
          unitNumber = "Unit",
          enlistmentLocation = "Location",
          dischargeLocation = "Location",
          selectiveServicesFlag = true,
          militaryRankCode = "MR1",
          militaryRankDescription = "Rank (Army)",
          serviceNumber = "SN1",
          disciplinaryActionCode = "DA1",
          disciplinaryActionDescription = "Disciplinary",
        ),
      ),
    )

    val PHYSICAL_ATTRIBUTES_PRISON_DTO = PhysicalAttributesPrisonDto(
      height = 180,
      weight = 75,
      hair = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      facialHair = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      face = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      build = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      leftEyeColour = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      rightEyeColour = ReferenceDataValuePrisonDto("DM", "CD", "Desc"),
      shoeSize = "10",
    )

    val DISTINGUISHING_MARK_PRISON_DTO = DistinguishingMarkPrisonDto(
      id = 1,
      bookingId = -1,
      offenderNo = PRISONER_NUMBER,
      bodyPart = ReferenceDataCode("DM", "CD", "Desc", "AF", 0),
      markType = ReferenceDataCode("DM", "CD", "Desc", "AF", 0),
      side = ReferenceDataCode("DM", "CD", "Desc", "AF", 0),
      partOrientation = ReferenceDataCode("DM", "CD", "Desc", "AF", 0),
      comment = "Comment",
      createdAt = LocalDateTime.now(),
      createdBy = "USER",
      photographUuids = listOf(),
    )

    val PHONE_1 = PhoneNumberPrisonDto(
      phoneId = 1L,
      type = "MOBILE",
      number = "07123456789",
      ext = "123",
    )

    val EMAIL_1 = EmailAddressPrisonDto(
      emailAddressId = 2L,
      email = "test@example.com",
    )

    val FULL_PERSON_PRISON_DTO = FullPersonPrisonDto(
      addresses = listOf(ADDRESS_PRISON_DTO),
      aliases = listOf(ALIAS),
      phones = listOf(PHONE_1),
      emails = listOf(EMAIL_1),
      militaryRecord = MILITARY_RECORD_PRISON_DTO,
      physicalAttributes = PHYSICAL_ATTRIBUTES_PRISON_DTO,
      distinguishingMarks = listOf(DISTINGUISHING_MARK_PRISON_DTO),
    )
  }
}
