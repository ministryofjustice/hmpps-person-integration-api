package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to prisoner nationality")
data class UpdateNationality(
  @Schema(description = "Nationality code", example = "BRIT", required = true, nullable = true)
  val nationality: String?,
)
