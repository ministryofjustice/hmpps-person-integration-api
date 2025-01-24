package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to other prisoner nationalities")
data class UpdateOtherNationalities(
  @Schema(description = "Other nationalities", example = "French", required = true, nullable = true)
  val otherNationalities: String?,
)
