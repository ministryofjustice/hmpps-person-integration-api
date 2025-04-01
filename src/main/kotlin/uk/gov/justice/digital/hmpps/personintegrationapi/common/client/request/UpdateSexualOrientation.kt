package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Update to prisoner sexual orientation")
data class UpdateSexualOrientation(
  @Schema(description = "Sexual orientation code (from the SEXO profile type)", example = "HET", required = true, nullable = true)
  val sexualOrientation: String?,
)
