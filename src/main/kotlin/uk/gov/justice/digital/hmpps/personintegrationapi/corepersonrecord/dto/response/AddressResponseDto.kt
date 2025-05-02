package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
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
    description = "ID of the pseudonym/alias for a person this address is associated with.",
    example = "true",
  )
  @field:NotNull
  val pseudonymId: Long,

  @Schema(
    description = "The unique property reference number for the address.",
    example = "000123456789",
    nullable = true,
  )
  val uprn: Long? = null,

  @Schema(description = "Boolean indicating if the person has no fixed abode.", example = "False", nullable = true)
  val noFixedAbode: Boolean? = null,

  @Schema(description = "Building name", example = "1", nullable = true)
  val buildingNumber: String? = null,

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
  val postTown: String? = null,

  @Schema(
    description = "County",
    nullable = true,
  )
  val county: ReferenceDataValue? = null,

  @Schema(
    description = "Country",
    nullable = false,
  )
  val country: ReferenceDataValue,

  @Schema(description = "Postcode", example = "A123BC", nullable = true)
  val postCode: String? = null,

  @Schema(
    description = "Date address is in use from.",
    example = "2025-01-01",
    nullable = false,
  )
  val fromDate: LocalDate,

  @Schema(
    description = "Date address is in use to.",
    example = "2025-01-02",
    nullable = true,
  )
  val toDate: LocalDate? = null,

  @Schema(
    description = "The address type (From ADDR_TYPE reference data).",
    example = "HOME",
    allowableValues = ["BUS", "HOME", "WORK"],
    nullable = false,
  )
  val addressType: String,

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
)
