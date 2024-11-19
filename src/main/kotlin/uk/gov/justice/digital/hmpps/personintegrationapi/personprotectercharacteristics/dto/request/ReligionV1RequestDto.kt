package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Religion V1 create/update request")
data class ReligionV1RequestDto(
  @Schema(
    description = "The religion code",
    example = "AGNO",
    required = true,
    nullable = false,
  )
  val religionCode: String,

  @Schema(
    description = "Reason for the religion change",
    example = "Religion has changed",
    required = false,
    nullable = false,
  )
  val reasonForChange: String,

  @Schema(
    description = "The date the religious belief is valid from",
    required = true,
    nullable = false,
  )
  val effectiveFromDate: LocalDate,

  @Schema(
    description = "Boolean indicating if the religious belief has been verified.",
    example = "false",
    defaultValue = "false",
    required = true,
    nullable = false,
  )
  val isVerified: Boolean,
)
