package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateEmailAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreatePhoneNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.ContactRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto

@Service
class ContactsService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun createContact(
    personId: String,
    contactRequest: ContactRequestDto,
  ): ResponseEntity<ContactResponseDto> {
    if (!isValidContact(contactRequest)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    val mappedResponse = if (contactRequest.contactType == "EMAIL") {
      val response = prisonApiClient.createEmailAddress(
        personId,
        CreateEmailAddress(
          emailAddress = contactRequest.contactValue,
        ),
      )
      if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

      response.body?.let { body ->
        ContactResponseDto(
          contactId = body.emailAddressId,
          contactType = "EMAIL",
          contactValue = body.email,
        )
      }
    } else {
      val response = prisonApiClient.createPhoneNumber(
        personId,
        CreatePhoneNumber(
          phoneNumberType = contactRequest.contactType,
          phoneNumber = contactRequest.contactValue,
          phoneNumberExtension = contactRequest.contactPhoneExtension,
        ),
      )

      if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

      response.body?.let { body ->
        ContactResponseDto(
          contactId = body.phoneId,
          contactType = body.type,
          contactValue = body.number,
          contactPhoneExtension = body.ext,
        )
      }
    }

    return ResponseEntity.ok(mappedResponse)
  }

  fun getContacts(
    personId: String,
  ): ResponseEntity<Collection<ContactResponseDto>> {
    val phoneNumbers = prisonApiClient.getPhoneNumbers(personId)
    val emailAddresses = prisonApiClient.getEmailAddresses(personId)

    if (!phoneNumbers.statusCode.is2xxSuccessful) return ResponseEntity.status(phoneNumbers.statusCode).build()
    if (!emailAddresses.statusCode.is2xxSuccessful) return ResponseEntity.status(emailAddresses.statusCode).build()

    val mappedResponse = phoneNumbers.body!!.map { number ->
      ContactResponseDto(
        number.phoneId,
        number.type,
        number.number,
        number.ext,
      )
    }.toMutableList()

    emailAddresses.body!!.forEach { email ->
      mappedResponse.add(
        ContactResponseDto(
          email.emailAddressId,
          "EMAIL",
          email.email,
        ),
      )
    }

    return ResponseEntity.ok(mappedResponse)
  }

  fun updateContact(
    personId: String,
    contactId: Long,
    contactRequest: ContactRequestDto,
  ): ResponseEntity<ContactResponseDto> {
    if (!isValidContact(contactRequest)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

    val mappedResponse = if (contactRequest.contactType == "EMAIL") {
      val response = prisonApiClient.updateEmailAddress(
        personId,
        contactId,
        CreateEmailAddress(
          emailAddress = contactRequest.contactValue,
        ),
      )

      if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

      response.body!!.let { body ->
        ContactResponseDto(
          contactId = body.emailAddressId,
          contactType = "EMAIL",
          contactValue = body.email,
        )
      }
    } else {
      val response = prisonApiClient.updatePhoneNumber(
        personId,
        contactId,
        CreatePhoneNumber(
          phoneNumberType = contactRequest.contactType,
          phoneNumber = contactRequest.contactValue,
          phoneNumberExtension = contactRequest.contactPhoneExtension,
        ),
      )

      if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

      response.body!!.let { body ->
        ContactResponseDto(
          contactId = body.phoneId,
          contactType = body.type,
          contactValue = body.number,
          contactPhoneExtension = body.ext,
        )
      }
    }

    return ResponseEntity.ok(mappedResponse)
  }

  private fun isValidContact(request: ContactRequestDto): Boolean = if (request.contactType == "EMAIL") {
    request.contactValue.length <= 240
  } else {
    if (request.contactValue.length > 40) {
      false
    } else {
      if (request.contactPhoneExtension.isNullOrEmpty()) {
        true
      } else {
        request.contactPhoneExtension.length <= 7
      }
    }
  }
}
