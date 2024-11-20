package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response object for field update requests")
data class FieldUpdateResponseDto(
  @Schema(description = "Response status for the request", example = "OK")
  val status: String,

  @Schema(
    description = "Optional response message",
    example = "BIRTHPLACE for prisoner A12345 successfully updated.",
  )
  val message: String,

  @Schema(
    description = "The field that has been updated",
    example = "BIRTHPLACE",
  )
  val fieldName: String,

  @Schema(
    description = "The updated field value",
    example = "London",
    nullable = false,
  )
  val fieldValue: String,
)
