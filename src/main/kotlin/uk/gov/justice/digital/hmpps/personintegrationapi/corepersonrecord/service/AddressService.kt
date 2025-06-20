package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.apache.commons.lang3.NotImplementedException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.AddressTypeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.AddressRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto

@Service
class AddressService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun createAddress(
    personId: String,
    addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = ResponseEntity.ok(
    AddressResponseDto(
      addressId = 12345,
      personId = personId,
      country = ReferenceDataValue("1", addressRequestDto.countryCode, "country description"),
      fromDate = addressRequestDto.fromDate,
      addressTypes = listOf<AddressTypeDto>(
        AddressTypeDto(
          ReferenceDataValue(
            "HOME",
            "HOME",
            "Home Address",
          ),
          true,
        ),
      ),
      addressPhoneNumbers = emptyList(),
    ),
  )

  fun getAddresses(
    personId: String,
  ): ResponseEntity<Collection<AddressResponseDto>> {
    val addresses = prisonApiClient.getAddresses(personId)

    if (!addresses.statusCode.is2xxSuccessful) return ResponseEntity.status(addresses.statusCode).build()

    val mappedResponse = addresses.body!!.map { address ->
      AddressResponseDto(
        addressId = address.addressId,
        personId = personId,
        uprn = null, // Not stored in NOMIS
        noFixedAbode = address.noFixedAddress,
        buildingNumber = address.flat,
        subBuildingName = null, // In NOMIS this is combined with buildingName and stored under 'premise'
        buildingName = address.premise,
        thoroughfareName = address.street,
        dependantLocality = address.locality,
        postTown = address.townCode?.let {
          ReferenceDataValue(
            id = "CITY_${address.townCode}",
            code = address.townCode,
            description = address.town!!,
          )
        },
        county = address.countyCode?.let {
          ReferenceDataValue(
            id = "COUNTY_${address.countyCode}",
            code = address.countyCode,
            description = address.county!!,
          )
        },
        country = address.countryCode?.let {
          ReferenceDataValue(
            id = "COUNTRY_${address.countryCode}",
            code = address.countryCode,
            description = address.country!!,
          )
        },
        postCode = address.postalCode,
        fromDate = address.startDate,
        toDate = address.endDate,
        addressTypes = address.addressUsages.map { usage ->
          AddressTypeDto(
            active = usage.activeFlag,
            addressUsageType = ReferenceDataValue(
              id = "ADDRESS_TYPE_${usage.addressUsage}",
              code = usage.addressUsage,
              description = usage.addressUsageDescription,
            ),
          )
        },
        primaryAddress = address.primary,
        postalAddress = address.mail,
        comment = address.comment,
        addressPhoneNumbers = address.phones.map { phone ->
          ContactResponseDto(
            contactId = phone.phoneId,
            contactType = phone.type,
            contactValue = phone.number,
            contactPhoneExtension = phone.ext,
          )
        },
      )
    }

    return ResponseEntity.ok(mappedResponse)
  }

  fun updateAddress(
    personId: String,
    addressId: Long,
    addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = throw NotImplementedException("Address update is not yet implemented.")
}
