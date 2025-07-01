package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreatePhoneNumber(
  @Schema(
    description = "Phone number type",
    example = "HOME",
    allowableValues = ["HOME", "BUS", "FAX", "ALTB", "ALTH", "MOB", "VISIT"],
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val phoneNumberType: String,

  @Schema(description = "Phone number", example = "01234 567 789")
  @field:Size(max = 40)
  @field:NotNull
  val phoneNumber: String,

  @Schema(description = "Phone number extension", example = "789")
  @field:Size(max = 7)
  val extension: String? = null,
)
