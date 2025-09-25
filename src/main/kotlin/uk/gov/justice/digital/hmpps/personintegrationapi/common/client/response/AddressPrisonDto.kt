package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.AddressTypeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto
import java.time.LocalDate

data class AddressPrisonDto(
  val addressId: Long,
  val addressType: String?,
  val flat: String?,
  val premise: String?,
  val street: String?,
  val locality: String?,
  val town: String?,
  val townCode: String?,
  val postalCode: String?,
  val county: String?,
  val countyCode: String?,
  val country: String?,
  val countryCode: String?,
  val comment: String?,
  val primary: Boolean,
  val mail: Boolean,
  val noFixedAddress: Boolean,
  val startDate: LocalDate?,
  val endDate: LocalDate?,
  val phones: List<Telephone>,
  val addressUsages: List<AddressUsage>,
) {
  fun toResponseDto(personId: String) = AddressResponseDto(
    addressId = this.addressId,
    personId = personId,
    uprn = null, // Not stored in NOMIS
    noFixedAbode = this.noFixedAddress,
    subBuildingName = this.flat,
    buildingNumber = null, // In NOMIS this is combined with buildingName and stored under 'premise'
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
    addressTypes = this.addressUsages
      .filter { it.addressUsage != null && it.addressUsageDescription != null }
      .map { usage ->
        AddressTypeDto(
          active = usage.activeFlag,
          addressUsageType = ReferenceDataValue(
            id = "ADDRESS_TYPE_${usage.addressUsage}",
            code = usage.addressUsage!!,
            description = usage.addressUsageDescription!!,
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

data class Telephone(
  val phoneId: Long,
  val number: String,
  val type: String,
  val ext: String?,
)

data class AddressUsage(
  val addressId: Long,
  val addressUsage: String?,
  val addressUsageDescription: String?,
  val activeFlag: Boolean,
)
