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
  fun toAddressResponseDto(personId: String) = AddressResponseDto(
    addressId = this.addressId,
    personId = personId,
    uprn = null,
    noFixedAbode = this.noFixedAddress,
    subBuildingName = this.flat,
    buildingNumber = null,
    buildingName = this.premise,
    thoroughfareName = this.street,
    dependantLocality = this.locality,
    postTown = this.townCode?.let {
      ReferenceDataValue("CITY_$it", it, this.town!!)
    },
    county = this.countyCode?.let {
      ReferenceDataValue("COUNTY_$it", it, this.county!!)
    },
    country = this.countryCode?.let {
      ReferenceDataValue("COUNTRY_$it", it, this.country!!)
    },
    postCode = this.postalCode,
    fromDate = this.startDate,
    toDate = this.endDate,
    addressTypes = this.addressUsages.map { usage ->
      AddressTypeDto(
        active = usage.activeFlag,
        addressUsageType = ReferenceDataValue(
          "ADDRESS_TYPE_${usage.addressUsage}",
          usage.addressUsage,
          usage.addressUsageDescription,
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
  val addressUsage: String,
  val addressUsageDescription: String,
  val activeFlag: Boolean,
)
