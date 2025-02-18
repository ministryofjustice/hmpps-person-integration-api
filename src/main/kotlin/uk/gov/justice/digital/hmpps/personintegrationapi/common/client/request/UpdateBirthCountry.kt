package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to prisoner birth country")
data class UpdateBirthCountry(
  @Schema(description = "Country code", example = "GBR", required = true, nullable = true)
  val countryCode: String?,
)
