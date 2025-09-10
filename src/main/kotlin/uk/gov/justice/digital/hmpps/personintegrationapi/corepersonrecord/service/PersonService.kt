package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PrisonerProfileSummaryResponseDto

@Service
class PersonService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getPerson(prisonerNumber: String): ResponseEntity<PrisonerProfileSummaryResponseDto?> {
    val response = prisonApiClient.getPrisonerProfileSummary(prisonerNumber)

    if (!response.statusCode.is2xxSuccessful) {
      return ResponseEntity.status(response.statusCode).build()
    }

    val body = response.body ?: return ResponseEntity.status(response.statusCode).build()
    val mappedResponse = PrisonerProfileSummaryResponseDto(
      addresses = body.addresses.map { it.toResponseDto(prisonerNumber) },
      pseudonyms = body.aliases.map { it.toResponseDto() },
      contacts = buildList {
        body.phones.forEach { add(ContactResponseDto(it.phoneId, it.type, it.number, it.ext)) }
        body.emails.forEach { add(ContactResponseDto(it.emailAddressId, "EMAIL", it.email)) }
      },
      militaryRecords = body.militaryRecord?.militaryRecords?.map { it.toResponseDto() } ?: emptyList(),
      physicalAttributes = body.physicalAttributes?.toResponseDto(),
      distinguishingMarks = body.distinguishingMarks.map { it.toResponseDto() },
    )

    return ResponseEntity.ok(mappedResponse)
  }
}
