package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.AddressRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
import java.time.LocalDate

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
      addressType = addressRequestDto.addressType,
    ),
  )

  fun getAddresses(
    personId: String,
  ): ResponseEntity<Collection<AddressResponseDto>> = ResponseEntity.ok(
    listOf(
      AddressResponseDto(
        addressId = 12345,
        personId = personId,
        country = ReferenceDataValue("1", "ENG", "England"),
        fromDate = LocalDate.now(),
        addressType = "HOME",
      ),
    ),
  )

  fun updateAddress(
    personId: String,
    addressId: Long,
    addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = ResponseEntity.ok(
    AddressResponseDto(
      addressId = addressId,
      personId = personId,
      country = ReferenceDataValue("1", addressRequestDto.countryCode, "country description"),
      fromDate = addressRequestDto.fromDate,
      addressType = addressRequestDto.addressType,
    ),
  )
}
