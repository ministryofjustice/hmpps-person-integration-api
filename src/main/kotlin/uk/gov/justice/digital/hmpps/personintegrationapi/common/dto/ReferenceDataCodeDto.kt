package uk.gov.justice.digital.hmpps.personintegrationapi.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Reference Data Code DTO")
@JsonInclude(NON_NULL)
data class ReferenceDataCodeDto(
  @Schema(description = "Id", example = "COUNTRY_GBR")
  val id: String,

  @Schema(description = "Short code for reference data code", example = "GBR")
  val code: String,

  @Schema(description = "Description of the reference data code", example = "United Kingdom")
  val description: String,

  @Schema(
    description = "The sequence number of the reference data code. " +
      "Used for ordering reference data correctly in lists and dropdowns. " +
      "0 is default order by description.",
    example = "3",
  )
  val listSequence: Int,

  @Schema(
    description = "Indicates that the reference data code is active and can be used. " +
      "Inactive reference data codes are not returned by default in the API",
    example = "true",
  )
  val isActive: Boolean,

  @Schema(description = "Parent code for the reference data code", example = "EU")
  val parentCode: String? = null,

  @Schema(description = "Parent domain for the reference data code", example = "REGION")
  val parentDomain: String? = null,
)
