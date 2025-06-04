package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateEmailAddress(
  @Schema(description = "Email address", example = "foo@bar.example")
  @field:Size(max = 240)
  @field:NotNull
  val emailAddress: String,
)
