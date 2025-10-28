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
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.IdentifierPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ImageDetailPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecord
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhysicalAttributesPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataValuePrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanResult
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanStatus
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.CreateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.UpdateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.DateOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanFailureException
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CorePersonRecordServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @Mock
  lateinit var referenceDataClient: ReferenceDataClient

  @Mock
  lateinit var documentApiClient: DocumentApiClient

  @InjectMocks
  lateinit var underTest: CorePersonRecordService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient, referenceDataClient, documentApiClient)
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
      whenever(prisonApiClient.getMilitaryRecords(PRISONER_NUMBER)).thenReturn(
        ResponseEntity.ok(militaryRecordsPrisonDto),
      )

      val response = underTest.getMilitaryRecords(PRISONER_NUMBER)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(militaryRecords)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.getMilitaryRecords(PRISONER_NUMBER)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.getMilitaryRecords(PRISONER_NUMBER)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class MilitaryRecordRequest {
    @Test
    fun `can create military records`() {
      whenever(prisonApiClient.createMilitaryRecord(PRISONER_NUMBER, CREATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(HttpStatus.CREATED).build(),
      )

      val response = underTest.createMilitaryRecord(PRISONER_NUMBER, CREATE_MILITARY_RECORD)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.createMilitaryRecord(PRISONER_NUMBER, CREATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.createMilitaryRecord(PRISONER_NUMBER, CREATE_MILITARY_RECORD)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdateMilitaryRecord {
    private val militarySeq = 1

    @Test
    fun `can update military records`() {
      whenever(prisonApiClient.updateMilitaryRecord(PRISONER_NUMBER, militarySeq, UPDATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.noContent().build(),
      )

      val response = underTest.updateMilitaryRecord(PRISONER_NUMBER, militarySeq, UPDATE_MILITARY_RECORD)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updateMilitaryRecord(PRISONER_NUMBER, militarySeq, UPDATE_MILITARY_RECORD)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updateMilitaryRecord(PRISONER_NUMBER, militarySeq, UPDATE_MILITARY_RECORD)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdateNationality {
    private val incomingRequest = UpdateNationality("BRIT", "French")

    @Test
    fun `can update the nationality field`() {
      whenever(prisonApiClient.updateNationalityForWorkingName(PRISONER_NUMBER, incomingRequest))
        .thenReturn(ResponseEntity.noContent().build())

      val response = underTest.updateNationality(PRISONER_NUMBER, incomingRequest)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updateNationalityForWorkingName(PRISONER_NUMBER, incomingRequest)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updateNationality(PRISONER_NUMBER, incomingRequest)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdateProfileImage {
    private val prisonerNumber = "A1234AA"

    @Test
    fun `can update the prisoners photo`() {
      whenever(prisonApiClient.updateProfileImage(MULTIPART_FILE, prisonerNumber, "GEN")).thenReturn(
        ResponseEntity.ok(
          ImageDetailPrisonDto(
            imageId = 1,
            imageOrientation = "A",
            imageType = "A",
            imageView = "A",
            active = true,
            captureDate = LocalDate.now(),
            captureDateTime = LocalDateTime.now(),
            objectId = 1234,
          ),
        ),
      )

      whenever(documentApiClient.virusScan(MULTIPART_FILE)).thenReturn(
        ResponseEntity.ok(
          DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE,
        ),
      )

      val response = underTest.updateProfileImage(MULTIPART_FILE, prisonerNumber, "GEN")

      assertThat(response.statusCode.value()).isEqualTo(204)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes from the Prison API`(status: Int) {
      whenever(prisonApiClient.updateProfileImage(MULTIPART_FILE, prisonerNumber, "GEN")).thenReturn(
        ResponseEntity.status(status).build(),
      )

      whenever(documentApiClient.virusScan(MULTIPART_FILE)).thenReturn(
        ResponseEntity.ok(
          DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE,
        ),
      )

      val response = underTest.updateProfileImage(MULTIPART_FILE, prisonerNumber, "GEN")
      assertThat(response.statusCode.value()).isEqualTo(status)
    }

    @Test
    fun `throws an exception if the virus scan fails`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE))
      assertThrows<VirusScanFailureException> {
        underTest.updateProfileImage(
          MULTIPART_FILE,
          PRISONER_NUMBER,
          "GEN",
        )
      }
    }
  }

  @Nested
  inner class UpdateExistingIdentifier {
    private val idSeq = 1L
    private val incomingRequest = UpdateIdentifierRequestDto("6697/56U", "Some comments")
    private val prisonApiRequest = UpdateIdentifier("006697/56U", "Some comments")

    @BeforeEach
    fun setup() {
      whenever(prisonApiClient.getAllIdentifiers(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(listOf(IdentifierPrisonDto("CRO", "SF81/58924V", null, 1, 1))))

      val aliasMock: CorePersonRecordAlias = mock()
      whenever(aliasMock.prisonerNumber).thenReturn(PRISONER_NUMBER)
      whenever(prisonApiClient.getAlias(OFFENDER_ID)).thenReturn(ResponseEntity.ok(aliasMock))
    }

    @Test
    fun `can update an existing identifier`() {
      whenever(prisonApiClient.updateIdentifier(OFFENDER_ID, idSeq, prisonApiRequest))
        .thenReturn(ResponseEntity.noContent().build())

      val response = underTest.updateIdentifier(OFFENDER_ID, idSeq, incomingRequest)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updateIdentifier(OFFENDER_ID, idSeq, prisonApiRequest)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updateIdentifier(OFFENDER_ID, idSeq, incomingRequest)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class AddIdentifiers {
    private val domain = "ID_TYPE"
    private val incomingRequest = listOf(
      CreateIdentifierRequestDto("CRO", "6697/56U", "Some comments"),
      CreateIdentifierRequestDto("HOREF", "1234", "Some more comments"),
    )
    private val prisonApiRequest = listOf(
      CreateIdentifier("CRO", "006697/56U", "Some comments"),
      CreateIdentifier("HOREF", "1234", "Some more comments"),
    )

    @BeforeEach
    fun setup() {
      val referenceCodes = listOf(
        ReferenceDataCode(domain, "CRO", "CRO", "Y", 1),
        ReferenceDataCode(domain, "HOREF", "Home office reference", "Y", 2),
      )
      whenever(referenceDataClient.getReferenceDataByDomain(domain))
        .thenReturn(ResponseEntity.ok(referenceCodes))
      whenever(prisonApiClient.getAllIdentifiers(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(listOf(IdentifierPrisonDto("CRO", "SF81/58924V", null, 1, 1))))
    }

    @Test
    fun `can add new identifiers`() {
      whenever(prisonApiClient.addIdentifiers(PRISONER_NUMBER, prisonApiRequest))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build())

      val response = underTest.addIdentifiers(PRISONER_NUMBER, incomingRequest)
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.addIdentifiers(PRISONER_NUMBER, prisonApiRequest)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.addIdentifiers(PRISONER_NUMBER, incomingRequest)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class GetPhysicalAttributes {
    private val physicalAttributesPrisonDto = PhysicalAttributesPrisonDto(
      height = 180,
      weight = 80,
      hair = ReferenceDataValuePrisonDto("HAIR", "BLACK", "Black"),
      facialHair = ReferenceDataValuePrisonDto("FACIAL_HAIR", "BEARDED", "Bearded"),
      face = ReferenceDataValuePrisonDto("FACE", "BULLET", "Bullet"),
      build = ReferenceDataValuePrisonDto("BUILD", "MEDIUM", "Medium"),
      leftEyeColour = ReferenceDataValuePrisonDto("L_EYE_C", "BLUE", "Blue"),
      rightEyeColour = ReferenceDataValuePrisonDto("R_EYE_C", "BLUE", "Blue"),
      shoeSize = "10",
    )

    private val physicalAttributes = PhysicalAttributesDto(
      height = 180,
      weight = 80,
      hair = ReferenceDataValue("HAIR_BLACK", "BLACK", "Black"),
      facialHair = ReferenceDataValue("FACIAL_HAIR_BEARDED", "BEARDED", "Bearded"),
      face = ReferenceDataValue("FACE_BULLET", "BULLET", "Long"),
      build = ReferenceDataValue("BUILD_MEDIUM", "MEDIUM", "Medium"),
      leftEyeColour = ReferenceDataValue("L_EYE_C_BLUE", "BLUE", "Blue"),
      rightEyeColour = ReferenceDataValue("R_EYE_C_BLUE", "BLUE", "Blue"),
      shoeSize = "10",
    )

    @Test
    fun `can retrieve physical attributes`() {
      whenever(prisonApiClient.getPhysicalAttributes(PRISONER_NUMBER)).thenReturn(
        ResponseEntity.ok(physicalAttributesPrisonDto),
      )

      val response = underTest.getPhysicalAttributes(PRISONER_NUMBER)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(physicalAttributes)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.getPhysicalAttributes(PRISONER_NUMBER)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.getPhysicalAttributes(PRISONER_NUMBER)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  @Nested
  inner class UpdatePhysicalAttributes {
    private val updateRequest = PhysicalAttributesRequest(height = 190)

    @Test
    fun `can update physical attributes`() {
      whenever(prisonApiClient.updatePhysicalAttributes(PRISONER_NUMBER, updateRequest)).thenReturn(
        ResponseEntity.noContent().build(),
      )

      val response = underTest.updatePhysicalAttributes(PRISONER_NUMBER, updateRequest)
      assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = [400, 401, 403, 404, 422, 500])
    fun `propagates non-2xx status codes`(status: Int) {
      whenever(prisonApiClient.updatePhysicalAttributes(PRISONER_NUMBER, updateRequest)).thenReturn(
        ResponseEntity.status(status).build(),
      )

      val response = underTest.updatePhysicalAttributes(PRISONER_NUMBER, updateRequest)
      assertThat(response.statusCode.value()).isEqualTo(status)
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val OFFENDER_ID = 1L
    const val TEST_BIRTHPLACE_VALUE = "London"
    const val TEST_COUNTRY_OF_BIRTH_VALUE = "ENG"
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

    val DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE = VirusScanResult(
      VirusScanStatus.PASSED,
      "Passed",
    )

    val MULTIPART_FILE: MultipartFile = MockMultipartFile(
      "file",
      "filename.jpg",
      MediaType.IMAGE_JPEG_VALUE,
      "I AM A JPEG, HONEST...".toByteArray(),
    )

    val DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE = VirusScanResult(
      VirusScanStatus.FAILED,
      "Failed",
    )
  }
}
