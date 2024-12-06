package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to prisoner birth place (city or town of birth)")
data class UpdateBirthPlace(
  @Schema(description = "Birth place (city or town of birth)", example = "SHEFFIELD", required = true, nullable = true)
  val birthPlace: String?,
)
