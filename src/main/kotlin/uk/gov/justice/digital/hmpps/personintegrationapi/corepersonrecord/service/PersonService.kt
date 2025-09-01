package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ResponseDtoMapper
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.FullPersonResponseDto

@Service
class PersonService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getPerson(prisonerNumber: String): ResponseEntity<FullPersonResponseDto> {
    val response = prisonApiClient.getFullPerson(prisonerNumber)
    val body = response.body ?: return ResponseEntity.status(response.statusCode).build()

    if (!response.statusCode.is2xxSuccessful) {
      return ResponseEntity.status(response.statusCode).build()
    }

    val mappedResponse = FullPersonResponseDto(
      addresses = body.addresses.map { ResponseDtoMapper.toAddressResponseDto(it, prisonerNumber) },
      pseudonyms = body.aliases.map { ResponseDtoMapper.toPseudonymResponseDto(it) },
      contacts = ResponseDtoMapper.toContactsResponseDto(body.phones, body.emails),
      militaryRecords = ResponseDtoMapper.toMilitaryRecordDtos(body.militaryRecord),
      physicalAttributes = ResponseDtoMapper.toPhysicalAttributesDto(body.physicalAttributes),
      distinguishingMarks = body.distinguishingMarks.map { ResponseDtoMapper.toDistinguishingMarkDto(it) },
    )

    return ResponseEntity.ok(mappedResponse)
  }

}
