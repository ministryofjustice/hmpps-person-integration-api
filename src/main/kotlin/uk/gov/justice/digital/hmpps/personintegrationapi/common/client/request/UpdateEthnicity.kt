package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to prisoner ethnicity")
data class UpdateEthnicity(
  @Schema(description = "Ethnicity code (from ETHNICITY reference domain)", example = "W1", required = true, nullable = true)
  val ethnicity: String?,
)
