package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Prisoner identifier create request")
data class CreateIdentifierRequestDto(
  @Schema(description = "Identifier type", example = "CRO", requiredMode = REQUIRED)
  @field:Size(max = 12)
  @field:NotBlank
  val type: String,
  @field:NotBlank
  @Schema(description = "Identifier value", example = "A123", requiredMode = REQUIRED)
  @field:Size(max = 20)
  @field:NotBlank
  val value: String,
  @Schema(description = "Comments", example = "Some information")
  @field:Size(max = 240)
  val comments: String?,
)
