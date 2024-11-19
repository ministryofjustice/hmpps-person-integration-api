package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.enumeration.ProtectedCharacteristicsField

@Schema(description = "Person Protected Characteristics V1 update request")
data class PersonProtectedCharacteristicsV1UpdateRequestDto(
  @Schema(
    description = "The field to be updated",
    example = "RELIGION",
    required = true,
    nullable = false,
  )
  val fieldName: ProtectedCharacteristicsField,

  @Schema(
    description = "The field to be updated",
    example = "CHRISTIAN",
    required = true,
    nullable = false,
  )
  val fieldValue: String,
)
