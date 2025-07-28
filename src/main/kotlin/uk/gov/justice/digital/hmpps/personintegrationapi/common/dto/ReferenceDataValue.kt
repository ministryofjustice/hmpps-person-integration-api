package uk.gov.justice.digital.hmpps.personintegrationapi.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Reference Data Value - a reference data code selected as the value for a field")
@JsonInclude(NON_NULL)
data class ReferenceDataValue(
//  @Schema(description = "Id", example = "id")
//  val id: String?,

  @Schema(description = "Code", example = "code")
  val code: String?,

  @Schema(description = "Description of the reference data code", example = "Code description")
  val description: String,
)
