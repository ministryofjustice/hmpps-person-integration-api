package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.AddressTypeDto
import java.time.LocalDate

@Schema(description = "Address response object.")
data class AddressResponseDto(
  @Schema(
    description = "The ID of the address.",
    example = "123456",
  )
  val addressId: Long,

  @Schema(
    description = "Person ID. Whilst proxying to NOMIS, this will be the prisoner number.",
    example = "A123456",
  )
  val personId: String,

  @Schema(
    description = "The unique property reference number for the address.",
    example = "000123456789",
    nullable = true,
  )
  val uprn: Long? = null,

  @Schema(
    description = "Boolean indicating if the person has no fixed abode.",
    example = "False",
    nullable = true,
  )
  val noFixedAbode: Boolean? = null,

  @Schema(description = "Building name", example = "1", nullable = true)
  val buildingNumber: String? = null,

  @Schema(description = "Sub building name", example = "Unit 1", nullable = true)
  val subBuildingName: String? = null,

  @Schema(description = "Building name", example = "The Building", nullable = true)
  val buildingName: String? = null,

  @Schema(description = "Thoroughfare name", example = "The Road", nullable = true)
  val thoroughfareName: String? = null,

  @Schema(
    description = "Dependant Locality, usually a child of the post town",
    example = "Small Village",
    nullable = true,
  )
  val dependantLocality: String? = null,

  @Schema(
    description = "Post town. This will be mapped to CITY reference data codes for persistence in " +
      "NOMIS. Any data without a mapping will not be persisted.",
    example = "My Town",
    nullable = true,
  )
  val postTown: ReferenceDataValue? = null,

  @Schema(
    description = "County",
    nullable = true,
  )
  val county: ReferenceDataValue? = null,

  @Schema(
    description = "Country",
    nullable = false,
  )
  val country: ReferenceDataValue? = null,

  @Schema(description = "Postcode", example = "A123BC", nullable = true)
  val postCode: String? = null,

  @Schema(
    description = "Date address is in use from.",
    example = "2025-01-01",
    nullable = false,
  )
  val fromDate: LocalDate? = null,

  @Schema(
    description = "Date address is in use to.",
    example = "2025-01-02",
    nullable = true,
  )
  val toDate: LocalDate? = null,

  @Schema(
    description = "Collection of address types applied to this address e.g. HOME (From ADDRESS_TYPE reference data).",
    nullable = false,
  )
  @field:NotNull val addressTypes: Collection<AddressTypeDto>,

  @Schema(
    description = "Boolean indicating whether to use this as the postal address",
    example = "True",
    nullable = true,
  )
  val postalAddress: Boolean? = null,

  @Schema(
    description = "Boolean indicating whether to use this as the primary address for the person",
    example = "True",
    nullable = true,
  )
  val primaryAddress: Boolean? = null,

  @Schema(
    description = "User entered comment describing the address",
    example = "Some comment",
    nullable = true,
  )
  val comment: String? = null,

  @Schema(
    description = "Phone numbers associated directly with the address",
    nullable = false,
  )
  @field:NotNull val addressPhoneNumbers: Collection<ContactResponseDto>,
)
