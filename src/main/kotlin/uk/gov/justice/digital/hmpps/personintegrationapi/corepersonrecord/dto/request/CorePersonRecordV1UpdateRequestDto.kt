package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.enumeration.CorePersonRecordField

@Schema(description = "Core Person Record V1 update request")
data class CorePersonRecordV1UpdateRequestDto(
  @Schema(
    description = "The field to be updated",
    example = "BIRTHPLACE",
    required = true,
    nullable = false,
  )
  val fieldName: CorePersonRecordField,

  @Schema(
    description = "The field to be updated",
    example = "London",
    required = true,
    nullable = false,
  )
  val fieldValue: String,
)
