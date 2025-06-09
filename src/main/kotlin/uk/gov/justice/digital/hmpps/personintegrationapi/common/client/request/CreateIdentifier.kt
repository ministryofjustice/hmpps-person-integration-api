package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Create a prisoner identifier")
data class CreateIdentifier(
  @Schema(description = "Identifier type", example = "CRO", requiredMode = REQUIRED)
  @field:NotBlank
  @field:Size(max = 12)
  val identifierType: String,
  @Schema(description = "Identifier value", example = "A123", requiredMode = REQUIRED)
  @field:NotBlank
  @field:Size(max = 20)
  val identifier: String,
  @Schema(description = "Comments", example = "Some information")
  @field:Size(max = 240)
  val issuedAuthorityText: String? = null,
)
