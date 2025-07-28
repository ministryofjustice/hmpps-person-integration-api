package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkImageDetailPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanResult
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanStatus
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanFailureException
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DistinguishingMarksServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @Mock
  lateinit var documentApiClient: DocumentApiClient

  @InjectMocks
  lateinit var underTest: DistinguishingMarksService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
    reset(documentApiClient)
  }

  @Nested
  inner class GetDistinguishingMarksForPrisonerLatestBooking {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.getDistinguishingMarks(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(listOf(DISTINGUISHING_MARK_PRISON_API_RESPONSE)))
    }

    @Test
    fun `can get all distinguishing marks for a prisoner's latest booking`() {
      val response = underTest.getDistinguishingMarks(PRISONER_NUMBER, SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(listOf(DISTINGUISHING_MARK))
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.getDistinguishingMarks(PRISONER_NUMBER, "UNKNOWN_SOURCE")
      }
    }
  }

  @Nested
  inner class GetDistinguishingMarkById {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.getDistinguishingMark(PRISONER_NUMBER, 1))
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK_PRISON_API_RESPONSE))
    }

    @Test
    fun `can get a distinguishing mark by id`() {
      val response = underTest.getDistinguishingMark(MARK_ID, SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(DISTINGUISHING_MARK)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.getDistinguishingMark(MARK_ID, "UNKNOWN_SOURCE")
      }
    }

    @DisplayName("throws an exception if the mark id is in an invalid format")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1234AA", "A1234AA-A", "A1234AA-1-2"])
    fun `throws an exception if the mark id is in an invalid format`(markId: String) {
      assertThrows<IllegalArgumentException> {
        underTest.getDistinguishingMark(markId, SOURCE_NOMIS)
      }
    }
  }

  @Nested
  inner class UpdateDistinguishingMark {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.updateDistinguishingMark(DISTINGUISHING_MARK_UPDATE_REQUEST, PRISONER_NUMBER, 1))
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK_PRISON_API_RESPONSE))
    }

    @Test
    fun `can update a distinguishing mark by id`() {
      val response = underTest.updateDistinguishingMark(DISTINGUISHING_MARK_UPDATE_REQUEST, MARK_ID, SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(DISTINGUISHING_MARK)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.updateDistinguishingMark(DISTINGUISHING_MARK_UPDATE_REQUEST, MARK_ID, "UNKNOWN_SOURCE")
      }
    }

    @DisplayName("throws an exception if the mark id is in an invalid format")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1234AA", "A1234AA-A", "A1234AA-1-2"])
    fun `throws an exception if the mark id is in an invalid format`(markId: String) {
      assertThrows<IllegalArgumentException> {
        underTest.updateDistinguishingMark(DISTINGUISHING_MARK_UPDATE_REQUEST, markId, SOURCE_NOMIS)
      }
    }
  }

  @Nested
  inner class CreateDistinguishingMark {
    @BeforeEach
    fun beforeEach() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE))
      whenever(
        prisonApiClient.createDistinguishingMark(
          MULTIPART_FILE,
          DISTINGUISHING_MARK_CREATE_REQUEST,
          PRISONER_NUMBER,
        ),
      ).thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK_PRISON_API_RESPONSE))
    }

    @Test
    fun `can create a distinguishing mark`() {
      val response = underTest.createDistinguishingMark(
        MULTIPART_FILE,
        DISTINGUISHING_MARK_CREATE_REQUEST,
        PRISONER_NUMBER,
        SOURCE_NOMIS,
      )
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(DISTINGUISHING_MARK)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.createDistinguishingMark(
          MULTIPART_FILE,
          DISTINGUISHING_MARK_CREATE_REQUEST,
          PRISONER_NUMBER,
          "UNKNOWN_SOURCE",
        )
      }
    }

    @Test
    fun `throws an exception if the virus scan fails`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE))
      assertThrows<VirusScanFailureException> {
        underTest.createDistinguishingMark(
          MULTIPART_FILE,
          DISTINGUISHING_MARK_CREATE_REQUEST,
          PRISONER_NUMBER,
          "UNKNOWN_SOURCE",
        )
      }
    }

    @Test
    fun `throws an exception if the virus scan errors`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity(HttpStatus.BAD_REQUEST))
      assertThrows<VirusScanException> {
        underTest.createDistinguishingMark(
          MULTIPART_FILE,
          DISTINGUISHING_MARK_CREATE_REQUEST,
          PRISONER_NUMBER,
          "UNKNOWN_SOURCE",
        )
      }
    }
  }

  @Nested
  inner class GetDistinguishingMarkImage {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.getDistinguishingMarkImage(1))
        .thenReturn(ResponseEntity.ok(IMAGE))
    }

    @Test
    fun `can get a distinguishing mark by id`() {
      val response = underTest.getDistinguishingMarkImage("1", SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(IMAGE)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.getDistinguishingMarkImage("1", "UNKNOWN_SOURCE")
      }
    }
  }

  @Nested
  inner class UpdateDistinguishingMarkImage {
    @BeforeEach
    fun beforeEach() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE))
      whenever(prisonApiClient.updateDistinguishingMarkImage(MULTIPART_FILE, 1))
        .thenReturn(ResponseEntity.ok(IMAGE))
    }

    @Test
    fun `can update a distinguishing mark by id`() {
      val response = underTest.updateDistinguishingMarkImage(MULTIPART_FILE, "1", SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(IMAGE)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.updateDistinguishingMarkImage(MULTIPART_FILE, "1", "UNKNOWN_SOURCE")
      }
    }

    @Test
    fun `throws an exception if the virus scan fails`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE))
      assertThrows<VirusScanFailureException> {
        underTest.updateDistinguishingMarkImage(MULTIPART_FILE, "1", SOURCE_NOMIS)
      }
    }

    @Test
    fun `throws an exception if the virus scan errors`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.badRequest().build())
      assertThrows<VirusScanException> {
        underTest.updateDistinguishingMarkImage(MULTIPART_FILE, "1", SOURCE_NOMIS)
      }
    }
  }

  @Nested
  inner class AddDistinguishingMarkImage {
    @BeforeEach
    fun beforeEach() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE))
      whenever(prisonApiClient.addDistinguishingMarkImage(MULTIPART_FILE, PRISONER_NUMBER, 1))
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK_PRISON_API_RESPONSE))
    }

    @Test
    fun `can add a distinguishing mark image`() {
      val response = underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(DISTINGUISHING_MARK)
    }

    @DisplayName("throws an exception if the mark id is in an invalid format")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1234AA", "A1234AA-A", "A1234AA-1-2"])
    fun `throws an exception if the mark id is in an invalid format`(markId: String) {
      assertThrows<IllegalArgumentException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, markId, SOURCE_NOMIS)
      }
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, "UNKNOWN_SOURCE")
      }
    }

    @Test
    fun `throws an exception if the virus scan fails`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.ok(DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE))
      assertThrows<VirusScanFailureException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, "UNKNOWN_SOURCE")
      }
    }

    @Test
    fun `throws an exception if the virus scan errors`() {
      whenever(documentApiClient.virusScan(MULTIPART_FILE))
        .thenReturn(ResponseEntity.badRequest().build())
      assertThrows<VirusScanException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, "UNKNOWN_SOURCE")
      }
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val MARK_ID = "A1234AA-1"
    const val SOURCE_NOMIS = "nomis"

    val DOCUMENT_API_VIRUS_SCAN_PASSED_RESPONSE = VirusScanResult(
      VirusScanStatus.PASSED,
      "Passed",
    )

    val DOCUMENT_API_VIRUS_SCAN_FAILED_RESPONSE = VirusScanResult(
      VirusScanStatus.FAILED,
      "Failed",
    )

    val DISTINGUISHING_MARK_PRISON_API_RESPONSE = DistinguishingMarkPrisonDto(
      id = 1,
      bookingId = -1,
      offenderNo = "A1234AA",
      bodyPart = ReferenceDataCode("BODY_PART", "LEG", "Leg", "Y", 1),
      markType = ReferenceDataCode("MARK_TYPE", "TAT", "Tattoo", "Y", 1),
      side = ReferenceDataCode("SIDE", "R", "Right", "Y", 1),
      partOrientation = ReferenceDataCode("PART_ORIENT", "LOW", "Low", "Y", 1),
      comment = "Some comment",
      createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
      createdBy = "USER",
      photographUuids = listOf(
        DistinguishingMarkImageDetailPrisonDto(100L, false),
        DistinguishingMarkImageDetailPrisonDto(101L, true),
      ),
    )

    val DISTINGUISHING_MARK = DistinguishingMarkDto(
      id = 1,
      bookingId = -1,
      offenderNo = "A1234AA",
      bodyPart = ReferenceDataValue(
//        "BODY_PART_LEG",
        "LEG",
        "Leg",
      ),
      markType = ReferenceDataValue(
//        "MARK_TYPE_TAT",
        "TAT",
        "Tattoo",
      ),
      side = ReferenceDataValue(
//        "SIDE_R",
        "R",
        "Right",
      ),
      partOrientation = ReferenceDataValue(
//        "PART_ORIENT_LOW",
        "LOW",
        "Low",
      ),
      comment = "Some comment",
      createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
      createdBy = "USER",
      photographUuids = listOf(
        DistinguishingMarkImageDetail(100L, false),
        DistinguishingMarkImageDetail(101L, true),
      ),
    )

    val DISTINGUISHING_MARK_UPDATE_REQUEST = DistinguishingMarkUpdateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Old wound",
    )

    val DISTINGUISHING_MARK_CREATE_REQUEST = DistinguishingMarkCreateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Old wound",
    )

    val IMAGE = "image".toByteArray()

    val MULTIPART_FILE: MultipartFile = MockMultipartFile(
      "file",
      "filename.jpg",
      MediaType.IMAGE_JPEG_VALUE,
      "I AM A JPEG, HONEST...".toByteArray(),
    )
  }
}
