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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DistinguishingMarksServiceTest {

  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  lateinit var underTest: DistinguishingMarksService

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @Nested
  inner class GetDistinguishingMarksForPrisonerLatestBooking {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.getDistinguishingMarks(PRISONER_NUMBER))
        .thenReturn(ResponseEntity.ok(listOf(DISTINGUISHING_MARK)))
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
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK))
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
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK))
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
      whenever(
        prisonApiClient.createDistinguishingMark(
          MULTIPART_FILE,
          DISTINGUISHING_MARK_CREATE_REQUEST,
          PRISONER_NUMBER,
        ),
      )
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK))
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
  inner class AddDistinguishingMarkImage {
    @BeforeEach
    fun beforeEach() {
      whenever(prisonApiClient.addDistinguishingMarkImage(MULTIPART_FILE, PRISONER_NUMBER, 1))
        .thenReturn(ResponseEntity.ok(DISTINGUISHING_MARK))
    }

    @Test
    fun `can add a distinguishing mark image`() {
      val response = underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, SOURCE_NOMIS)
      assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
      assertThat(response.body).isEqualTo(DISTINGUISHING_MARK)
    }

    @Test
    fun `throws an exception if the source system is not supported`() {
      assertThrows<IllegalArgumentException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, MARK_ID, "UNKNOWN_SOURCE")
      }
    }

    @DisplayName("throws an exception if the mark id is in an invalid format")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1234AA", "A1234AA-A", "A1234AA-1-2"])
    fun `throws an exception if the mark id is in an invalid format`(markId: String) {
      assertThrows<IllegalArgumentException> {
        underTest.addDistinguishingMarkImage(MULTIPART_FILE, markId, SOURCE_NOMIS)
      }
    }
  }

  private companion object {
    const val PRISONER_NUMBER = "A1234AA"
    const val MARK_ID = "A1234AA-1"
    const val SOURCE_NOMIS = "nomis"

    val DISTINGUISHING_MARK = DistinguishingMarkDto(
      id = 1,
      bookingId = -1,
      offenderNo = "A1234AA",
      bodyPart = "LEG",
      markType = "TAT",
      side = "R",
      partOrientation = "LOW",
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
