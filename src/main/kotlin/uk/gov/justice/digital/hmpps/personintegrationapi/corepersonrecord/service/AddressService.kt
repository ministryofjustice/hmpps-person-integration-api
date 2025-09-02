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

    val mappedResponse = response.body!!.toResponseDto(personId)
    return ResponseEntity.ok(mappedResponse)
  }

  fun getAddresses(
    personId: String,
  ): ResponseEntity<Collection<AddressResponseDto>> {
    val addresses = prisonApiClient.getAddresses(personId)

    if (!addresses.statusCode.is2xxSuccessful) return ResponseEntity.status(addresses.statusCode).build()

    val mappedResponse = addresses.body!!.map { it.toResponseDto(personId) }
    return ResponseEntity.ok(mappedResponse)
  }

  fun updateAddress(
    personId: String,
    addressId: Long,
    addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = throw NotImplementedException("Address update is not yet implemented.")

  private fun AddressRequestDto.toPrisonApiRequest(): CreateAddress {
    val premiseConstituents = listOf(
      subBuildingName,
      buildingName,
      buildingNumber,
    ).filter { !it.isNullOrBlank() }

    val premise = premiseConstituents.joinToString(separator = ", ").ifBlank { null }

    return CreateAddress(
      noFixedAddress = noFixedAbode,
      flat = null,
      premise = premise,
      street = thoroughfareName,
      locality = dependantLocality,
      townCode = postTownCode,
      countyCode = countyCode,
      countryCode = countryCode,
      postalCode = postCode,
      startDate = fromDate,
      endDate = toDate,
      addressUsages = addressTypes ?: emptyList(),
      primary = primaryAddress,
      mail = postalAddress,
    )
  }
}
