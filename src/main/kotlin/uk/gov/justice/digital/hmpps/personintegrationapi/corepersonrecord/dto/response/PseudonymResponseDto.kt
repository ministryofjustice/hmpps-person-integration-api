package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import java.time.LocalDate
import java.util.UUID

@Schema(description = "Pseudonym request object. Used to create or update a pseudonym.")
data class PseudonymResponseDto(
  @Schema(
    description = "Person ID. Whilst proxying to NOMIS, this will be null.",
    example = "123e4567-e89b-12d3-a456-426614174000",
    nullable = true,
  )
  val personId: UUID? = null,

  @Schema(description = "Source system ID", example = "1234")
  val sourceSystemId: Long,

  @Schema(description = "Source system", example = "NOMIS", allowableValues = ["NOMIS"])
  val sourceSystem: SourceSystem,

  @Schema(description = "Prisoner number", example = "A1234AA")
  val prisonerNumber: String,

  @Schema(
    description = "Boolean flag to indicate if the pseudonym is the working name",
    example = "true",
  )
  val isWorkingName: Boolean,

  @Schema(description = "First name", example = "John", requiredMode = REQUIRED)
  val firstName: String,

  @Schema(description = "Middle name", example = "Middlename")
  val middleName: String? = null,

  @Schema(description = "Last name", example = "Smith", requiredMode = REQUIRED)
  val lastName: String,

  @Schema(
    description = "Date of birth. Must be specified in YYYY-MM-DD format. Range allowed is 16-110 years",
    example = "1970-01-01",
  )
  val dateOfBirth: LocalDate,

  @Schema(description = "The name type (from NAME_TYPE reference domain)")
  val nameType: ReferenceDataValue? = null,

  @Schema(description = "A code representing the person's title (from TITLE reference domain).")
  val title: ReferenceDataValue? = null,

  @Schema(description = "A code representing the person's sex (from SEX reference domain).")
  val sex: ReferenceDataValue? = null,

  @Schema(description = "A code representing the person's ethnicity (from ETHNICITY reference domain).")
  val ethnicity: ReferenceDataValue? = null,
)
