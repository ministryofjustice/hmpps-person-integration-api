package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.toSourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto

@Service
class DistinguishingMarksService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getDistinguishingMarks(
    prisonerNumber: String,
    sourceSystem: String,
  ): ResponseEntity<List<DistinguishingMarkDto>> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> prisonApiClient.getDistinguishingMarks(prisonerNumber)
  }

  fun getDistinguishingMark(markId: String, sourceSystem: String): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> {
      val (prisonerNumber, sequenceId) = parseMarkId(markId)
      prisonApiClient.getDistinguishingMark(prisonerNumber, sequenceId)
    }
  }

  fun updateDistinguishingMark(
    request: DistinguishingMarkUpdateRequest,
    markId: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> {
      val (prisonerNumber, sequenceId) = parseMarkId(markId)
      prisonApiClient.updateDistinguishingMark(request, prisonerNumber, sequenceId)
    }
  }

  fun createDistinguishingMark(
    file: MultipartFile?,
    request: DistinguishingMarkCreateRequest,
    prisonerNumber: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> prisonApiClient.createDistinguishingMark(file, request, prisonerNumber)
  }

  fun getDistinguishingMarkImage(imageId: String, sourceSystem: String): ResponseEntity<ByteArray> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> prisonApiClient.getDistinguishingMarkImage(imageId.toInt())
  }

  fun addDistinguishingMarkImage(
    file: MultipartFile,
    markId: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> {
      val (prisonerNumber, sequenceId) = parseMarkId(markId)
      prisonApiClient.addDistinguishingMarkImage(file, prisonerNumber, sequenceId)
    }
  }

  private fun parseMarkId(markId: String): Pair<String, Int> {
    val tokens = markId.trim().uppercase().split("-")
    if (tokens.size != 2) {
      throw IllegalArgumentException("Unable to parse mark id: $markId")
    }
    return Pair(tokens[0], tokens[1].toInt())
  }
}
