package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidAllowableValues

@Schema(description = "Contact request object. Used to create or update a contact phone number or email address.")
data class ContactRequestDto(
  @Schema(
    description = "Contact type",
    example = "HOME",
    allowableValues = ["HOME", "BUS", "FAX", "ALTB", "ALTH", "MOB", "VISIT", "EMAIL"],
    requiredMode = REQUIRED,
  )
  @field:NotNull
  @field:NotBlank
  @ValidAllowableValues(allowableValues = ["HOME", "BUS", "FAX", "ALTB", "ALTH", "MOB", "VISIT", "EMAIL"])
  val contactType: String,

  @Schema(description = "Contact value", example = "01234 567 789")
  @field:Size(max = 240)
  @field:NotNull
  @field:NotBlank
  val contactValue: String,
)
