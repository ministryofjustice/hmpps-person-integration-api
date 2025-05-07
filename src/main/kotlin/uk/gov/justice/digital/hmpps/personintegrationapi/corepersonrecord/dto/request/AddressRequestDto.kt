package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "Address request object. Used to create or update an address.")
data class AddressRequestDto(

  @Schema(
    description = "The unique property reference number for the address.",
    example = "000123456789",
  ) val uprn: Long? = null,

  @Schema(
    description = "Boolean indicating if the person has no fixed abode.",
    example = "False",
  ) val noFixedAbode: Boolean? = null,

  @Schema(
    description = "Building name",
    example = "1",
  ) @field:Size(max = 4) val buildingNumber: String? = null,

  @Schema(
    description = "Sub building name",
    example = "Unit 1",
  ) @field:Size(max = 30) val subBuildingName: String? = null,

  @Schema(
    description = "Building name",
    example = "The Building",
  ) @field:Size(max = 50) val buildingName: String? = null,

  @Schema(
    description = "Thoroughfare name",
    example = "The Road",
  ) @field:Size(max = 160) val thoroughfareName: String? = null,

  @Schema(
    description = "Dependant Locality, usually a child of the post town",
    example = "Small Village",
  ) @field:Size(max = 70) val dependantLocality: String? = null,

  @Schema(
    description = "Reference data code for the post town. This is only in used while NOMIS is the master data store.",
    example = "LOND1",
  ) @field:Size(max = 12) val postTownCode: String? = null,

  @Schema(
    description = "County Code (From COUNTRY reference data).",
    example = "ENG",
  ) @field:Size(max = 12) val countyCode: String? = null,

  @Schema(
    description = "Country Code (From COUNTRY reference data).",
    example = "ENG",
    requiredMode = REQUIRED,
  ) @field:Size(max = 12) @field:NotNull val countryCode: String,

  @Schema(
    description = "Postcode",
    example = "A123BC",
  ) @field:Size(max = 8) val postCode: String? = null,

  @Schema(
    description = "Date address is in use from.",
    example = "2025-01-01",
    requiredMode = REQUIRED,
  ) @field:NotNull val fromDate: LocalDate,

  @Schema(
    description = "Date address is in use to.",
    example = "2025-01-02",
  ) val toDate: LocalDate? = null,

  @Schema(
    description = "The address type (From ADDR_TYPE reference data).",
    example = "HOME",
    allowableValues = ["BUS", "HOME", "WORK"],
    requiredMode = REQUIRED,
  ) @field:NotNull val addressType: String,

  @Schema(
    description = "Boolean indicating whether to use this as the postal address",
    example = "True",
  ) val postalAddress: Boolean? = null,

  @Schema(
    description = "Boolean indicating whether to use this as the primary address for the person",
    example = "True",
  ) val primaryAddress: Boolean? = null,
)
