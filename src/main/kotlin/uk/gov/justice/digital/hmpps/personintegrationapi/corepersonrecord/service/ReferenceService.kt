package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.ReferenceRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ReferenceResponseDto

@Service
class ReferenceService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun createReference(
    personId: String,
    referenceRequest: ReferenceRequestDto,
  ): ResponseEntity<ReferenceResponseDto> = ResponseEntity.ok(
    ReferenceResponseDto(
      personId,
      referenceRequest.pseudonymId,
      12345,
      referenceRequest.identifierValue,
      ReferenceDataValue("ID_TYPE", referenceRequest.identifierType, "Driving Licence"),
    ),
  )

  fun getReferences(
    personId: String,
    includePseudonyms: Boolean,
  ): ResponseEntity<Collection<ReferenceResponseDto>> = ResponseEntity.ok(
    listOf(
      ReferenceResponseDto(
        personId,
        123456,
        12345,
        "ID VALUE",
        ReferenceDataValue("ID_TYPE", "DL", "Driving Licence"),
      ),
    ),
  )

  fun updateReference(
    personId: String,
    referenceId: Long,
    referenceRequest: ReferenceRequestDto,
  ): ResponseEntity<ReferenceResponseDto> = ResponseEntity.ok(
    ReferenceResponseDto(
      personId,
      referenceRequest.pseudonymId,
      referenceId,
      referenceRequest.identifierValue,
      ReferenceDataValue("ID_TYPE", referenceRequest.identifierType, "Driving Licence"),
    ),
  )
}
