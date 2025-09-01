package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

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
)

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
