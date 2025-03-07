package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.toSourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanResult
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanStatus
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanFailureException

@Service
class DistinguishingMarksService(
  private val prisonApiClient: PrisonApiClient,
  private val documentApiClient: DocumentApiClient,
) {
  fun getDistinguishingMarks(
    prisonerNumber: String,
    sourceSystem: String,
  ): ResponseEntity<List<DistinguishingMarkDto>> {
    when (sourceSystem.toSourceSystem()) {
      NOMIS -> {
        val response = prisonApiClient.getDistinguishingMarks(prisonerNumber)

        return if (response.statusCode.is2xxSuccessful) {
          ResponseEntity.ok(response.body?.map { toDto(it) })
        } else {
          ResponseEntity.status(response.statusCode).build()
        }
      }
    }
  }

  fun getDistinguishingMark(markId: String, sourceSystem: String): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> {
      val (prisonerNumber, sequenceId) = parseMarkId(markId)
      mappedResponse(prisonApiClient.getDistinguishingMark(prisonerNumber, sequenceId))
    }
  }

  fun updateDistinguishingMark(
    request: DistinguishingMarkUpdateRequest,
    markId: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> {
      val (prisonerNumber, sequenceId) = parseMarkId(markId)
      mappedResponse(prisonApiClient.updateDistinguishingMark(request, prisonerNumber, sequenceId))
    }
  }

  fun createDistinguishingMark(
    file: MultipartFile?,
    request: DistinguishingMarkCreateRequest,
    prisonerNumber: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> {
    virusScan(file)
    return when (sourceSystem.toSourceSystem()) {
      NOMIS -> mappedResponse(prisonApiClient.createDistinguishingMark(file, request, prisonerNumber))
    }
  }

  fun getDistinguishingMarkImage(imageId: String, sourceSystem: String): ResponseEntity<ByteArray> = when (sourceSystem.toSourceSystem()) {
    NOMIS -> prisonApiClient.getDistinguishingMarkImage(imageId.toLong())
  }

  fun updateDistinguishingMarkImage(
    file: MultipartFile,
    imageId: String,
    sourceSystem: String,
  ): ResponseEntity<ByteArray> {
    virusScan(file)
    return when (sourceSystem.toSourceSystem()) {
      NOMIS -> prisonApiClient.updateDistinguishingMarkImage(file, imageId.toLong())
    }
  }

  fun addDistinguishingMarkImage(
    file: MultipartFile,
    markId: String,
    sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> {
    virusScan(file)
    return when (sourceSystem.toSourceSystem()) {
      NOMIS -> {
        val (prisonerNumber, sequenceId) = parseMarkId(markId)
        mappedResponse(prisonApiClient.addDistinguishingMarkImage(file, prisonerNumber, sequenceId))
      }
    }
  }

  private fun virusScan(file: MultipartFile?) {
    if (file != null) {
      try {
        val scanResult = documentApiClient.virusScan(file)

        if (scanResult.statusCode.is2xxSuccessful) {
          val result = scanResult.body as VirusScanResult
          when (result.status) {
            VirusScanStatus.FAILED -> throw VirusScanFailureException("Virus scan failed - ${result.result}")
            VirusScanStatus.ERROR -> throw VirusScanFailureException("Virus scan error - ${result.result}")
            VirusScanStatus.PASSED -> {}
          }
        } else {
          throw VirusScanException()
        }
      } catch (e: InternalServerError) {
        throw VirusScanException()
      }
    }
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
