package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotNull

@Schema(description = "Reference request object. Used to create or update a reference.")
data class ReferenceRequestDto(
  @Schema(
    description = "The ID of the person pseudonym the reference should be associated with. " +
      "While NOMIS is the data source this will be the offender ID of the alias.",
    example = "true",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val pseudonymId: Long,

  @Schema(
    description = "The identifier value",
    example = "ABC123",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val identifierValue: String,

  @Schema(
    description = "The identifier type",
    example = "DL",
    requiredMode = REQUIRED,
    allowableValues = ["CID", "CRO", "DL", "HMPS", "HOREF", "LIDS", "NINO", "NPD", "PARK", "PASS", "PNC", "PORT REF", "SPNC", "STAFF", "TBRI", "ULN", "YJAF"],
  )
  @field:NotNull
  val identifierType: String,

  @Schema(
    description = "Additional comments associated with the identifier",
    example = "European driving licence",
    requiredMode = NOT_REQUIRED,
    maxLength = 240,
  )
  val comments: String? = null,
)
