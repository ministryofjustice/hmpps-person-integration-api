package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.ContactRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto

@Service
class ContactsService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun createContact(
    personId: String,
    contactRequest: ContactRequestDto,
  ): ResponseEntity<ContactResponseDto> = ResponseEntity.ok(
    ContactResponseDto(
      personId,
      contactRequest.contactType,
      contactRequest.contactValue,
    ),
  )

  fun getContacts(
    personId: String,
  ): ResponseEntity<Collection<ContactResponseDto>> = ResponseEntity.ok(
    listOf(
      ContactResponseDto(
        personId,
        "HOME",
        "01234 567 891",
      ),
    ),
  )

  fun updateContact(
    personId: String,
    contactId: Long,
    contactRequest: ContactRequestDto,
  ): ResponseEntity<ContactResponseDto> = ResponseEntity.ok(
    ContactResponseDto(
      personId,
      contactRequest.contactType,
      contactRequest.contactValue,
    ),
  )
}
