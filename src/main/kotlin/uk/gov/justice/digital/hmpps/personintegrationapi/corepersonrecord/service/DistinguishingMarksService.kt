package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.util.virusScan
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail

@Service
class DistinguishingMarksService(
  private val prisonApiClient: PrisonApiClient,
  private val documentApiClient: DocumentApiClient,
) {
  fun getDistinguishingMarks(
    personId: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<List<DistinguishingMarkDto>> {
    val response = prisonApiClient.getDistinguishingMarks(personId)
    return if (response.statusCode.is2xxSuccessful) {
      ResponseEntity.ok(response.body?.map { toDto(it) })
    } else {
      ResponseEntity.status(response.statusCode).build()
    }
  }

  fun getDistinguishingMark(markId: String, sourceSystem: String = "REMOVE_ME_FOR_V2"): ResponseEntity<DistinguishingMarkDto> {
    val (personId, sequenceId) = parseMarkId(markId)
    return mappedResponse(prisonApiClient.getDistinguishingMark(personId, sequenceId))
  }

  fun updateDistinguishingMark(
    request: DistinguishingMarkUpdateRequest,
    markId: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<DistinguishingMarkDto> {
    val (personId, sequenceId) = parseMarkId(markId)
    return mappedResponse(prisonApiClient.updateDistinguishingMark(request, personId, sequenceId))
  }

  fun createDistinguishingMark(
    file: MultipartFile?,
    request: DistinguishingMarkCreateRequest,
    personId: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<DistinguishingMarkDto> {
    virusScan(file, documentApiClient)
    return mappedResponse(prisonApiClient.createDistinguishingMark(file, request, personId))
  }

  fun getDistinguishingMarkImage(imageId: String, sourceSystem: String = "REMOVE_ME_FOR_V2"): ResponseEntity<ByteArray> = prisonApiClient.getDistinguishingMarkImage(imageId.toLong())

  fun updateDistinguishingMarkImage(
    file: MultipartFile,
    imageId: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<ByteArray> {
    virusScan(file, documentApiClient)
    return prisonApiClient.updateDistinguishingMarkImage(file, imageId.toLong())
  }

  fun addDistinguishingMarkImage(
    file: MultipartFile,
    markId: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<DistinguishingMarkDto> {
    virusScan(file, documentApiClient)
    val (personId, sequenceId) = parseMarkId(markId)
    return mappedResponse(prisonApiClient.addDistinguishingMarkImage(file, personId, sequenceId))
  }

  private fun parseMarkId(markId: String): Pair<String, Int> {
    val tokens = markId.trim().uppercase().split("-")
    if (tokens.size != 2) {
      throw IllegalArgumentException("Unable to parse mark id: $markId")
    }
    return Pair(tokens[0], tokens[1].toInt())
  }

  private fun mappedResponse(response: ResponseEntity<DistinguishingMarkPrisonDto>): ResponseEntity<DistinguishingMarkDto> = if (response.statusCode.is2xxSuccessful) {
    ResponseEntity.ok(response.body?.let { toDto(it) })
  } else {
    ResponseEntity.status(response.statusCode).build()
  }

  private fun toDto(value: DistinguishingMarkPrisonDto): DistinguishingMarkDto = DistinguishingMarkDto(
    id = value.id,
    bookingId = value.bookingId,
    offenderNo = value.offenderNo,
    bodyPart = value.bodyPart?.toReferenceDataValue(),
    markType = value.markType?.toReferenceDataValue(),
    side = value.side?.toReferenceDataValue(),
    partOrientation = value.partOrientation?.toReferenceDataValue(),
    comment = value.comment,
    createdAt = value.createdAt,
    createdBy = value.createdBy,
    photographUuids = value.photographUuids.map { DistinguishingMarkImageDetail(it.id, it.latest) },
  )
}
