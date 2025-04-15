package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue

@Schema(description = "Reference request and response object. Used to create or update a reference.")
data class ReferenceResponseDto(
  @Schema(
    description = "The ID of the person the reference is associated with. " +
      "While NOMIS is the data source the person ID will be the prisoner number.",
    example = "true",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val personId: String,

  @Schema(
    description = "The ID of the person pseudonym the reference should be associated with",
    example = "true",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val pseudonymId: Long,

  @Schema(
    description = "The reference ID.",
    example = "true",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val referenceId: Long,

  @Schema(
    description = "The identifier value",
    example = "ABC123",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val identifierValue: String,

  @Schema(
    description = "The identifier type e.g. Driving Licence",
    example = "DL",
  )
  val identifierType: ReferenceDataValue,

  @Schema(
    description = "Additional comments about the identifier.",
    example = "European diving licence",
  )
  val comments: String? = null,
)
