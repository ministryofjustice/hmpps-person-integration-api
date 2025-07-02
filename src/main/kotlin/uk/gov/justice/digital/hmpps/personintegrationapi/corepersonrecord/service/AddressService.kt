package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.apache.commons.lang3.NotImplementedException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressPrisonDto
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
  ): ResponseEntity<AddressResponseDto> {
    val response = prisonApiClient.createAddress(personId, addressRequestDto.toPrisonApiRequest())

    if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

    val mappedResponse = response.body!!.toAddressResponseDto(personId)
    return ResponseEntity.ok(mappedResponse)
  }

  fun getAddresses(
    personId: String,
  ): ResponseEntity<Collection<AddressResponseDto>> {
    val addresses = prisonApiClient.getAddresses(personId)

    if (!addresses.statusCode.is2xxSuccessful) return ResponseEntity.status(addresses.statusCode).build()

    val mappedResponse = addresses.body!!.map { it.toAddressResponseDto(personId) }
    return ResponseEntity.ok(mappedResponse)
  }

  fun updateAddress(
    personId: String,
    addressId: Long,
    addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = throw NotImplementedException("Address update is not yet implemented.")

  private fun AddressRequestDto.toPrisonApiRequest() = CreateAddress(
    noFixedAddress = this.noFixedAbode,
    flat = this.buildingNumber,
    premise = "${this.subBuildingName.let { "$it " }}${this.buildingName}",
    street = this.thoroughfareName,
    locality = this.dependantLocality,
    townCode = this.postTownCode,
    countyCode = this.countyCode,
    countryCode = this.countryCode,
    postalCode = this.postCode,
    startDate = this.fromDate,
    endDate = this.toDate,
    addressUsages = this.addressTypes,
    primary = this.primaryAddress,
    mail = this.postalAddress,
  )

  private fun AddressPrisonDto.toAddressResponseDto(personId: String) = AddressResponseDto(
    addressId = this.addressId,
    personId = personId,
    uprn = null, // Not stored in NOMIS
    noFixedAbode = this.noFixedAddress,
    buildingNumber = this.flat,
    subBuildingName = null, // In NOMIS this is combined with buildingName and stored under 'premise'
    buildingName = this.premise,
    thoroughfareName = this.street,
    dependantLocality = this.locality,
    postTown = this.townCode?.let {
      ReferenceDataValue(
        id = "CITY_${this.townCode}",
        code = this.townCode,
        description = this.town!!,
      )
    },
    county = this.countyCode?.let {
      ReferenceDataValue(
        id = "COUNTY_${this.countyCode}",
        code = this.countyCode,
        description = this.county!!,
      )
    },
    country = this.countryCode?.let {
      ReferenceDataValue(
        id = "COUNTRY_${this.countryCode}",
        code = this.countryCode,
        description = this.country!!,
      )
    },
    postCode = this.postalCode,
    fromDate = this.startDate,
    toDate = this.endDate,
    addressTypes = this.addressUsages.map { usage ->
      AddressTypeDto(
        active = usage.activeFlag,
        addressUsageType = ReferenceDataValue(
          id = "ADDRESS_TYPE_${usage.addressUsage}",
          code = usage.addressUsage,
          description = usage.addressUsageDescription,
        ),
      )
    },
    primaryAddress = this.primary,
    postalAddress = this.mail,
    comment = this.comment,
    addressPhoneNumbers = this.phones.map { phone ->
      ContactResponseDto(
        contactId = phone.phoneId,
        contactType = phone.type,
        contactValue = phone.number,
        contactPhoneExtension = phone.ext,
      )
    },
  )
}
