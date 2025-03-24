package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "Pseudonym request object. Used to create or update a pseudonym.")
data class PseudonymRequestDto(
  @Schema(
    description = "Boolean flag to indicate if the pseudonym is the working name",
    example = "true",
    requiredMode = REQUIRED,
  )
  @field:NotNull
  val isWorkingName: Boolean,

  @Schema(description = "First name", example = "John", requiredMode = REQUIRED)
  @field:Size(max = 35)
  @field:NotBlank
  @field:Pattern(regexp = "^[A-Z|a-z ,.'-]+$", message = "First name is not valid")
  val firstName: String,

  @Schema(description = "Middle name 1", example = "Middleone")
  @field:Size(max = 35)
  @field:Pattern(regexp = "^[A-Z|a-z ,.'-]+$", message = "Middle name 1 is not valid")
  val middleName1: String? = null,

  @Schema(description = "Middle name 2", example = "Middletwo")
  @field:Size(max = 35)
  @field:Pattern(regexp = "^[A-Z|a-z ,.'-]+$", message = "Middle name 2 is not valid")
  val middleName2: String? = null,

  @Schema(description = "Last name", example = "Smith", requiredMode = REQUIRED)
  @field:Size(max = 35)
  @field:NotBlank
  @field:Pattern(regexp = "^[A-Z|a-z ,.'-]+$", message = "Last name is not valid")
  val lastName: String,

  @Schema(
    requiredMode = REQUIRED,
    description = "Date of birth. Must be specified in YYYY-MM-DD format. Range allowed is 16-110 years",
    example = "1970-01-01",
  )
  @field:NotNull
  val dateOfBirth: LocalDate,

  @Schema(
    description = "The name type (from NAME_TYPE reference domain)",
    example = "CN",
    allowableValues = ["A", "CN", "MAID", "NICK"],
  )
  val nameType: String? = null,

  @Schema(
    description = "A code representing the person's title (from TITLE reference domain).",
    example = "MR",
    allowableValues = ["BR", "DAME", "DR", "FR", "IMAM", "LADY", "LORD", "MISS", "MR", "MRS", "MS", "RABBI", "REV", "SIR", "SR"],
  )
  val title: String? = null,

  @Schema(
    description = "A code representing the person's sex (from SEX reference domain).",
    requiredMode = REQUIRED,
    example = "F",
    allowableValues = ["M", "F", "NK", "NS", "REF"],
  )
  @field:NotBlank
  val sex: String,

  @Schema(
    description = "A code representing the person's ethnicity (from ETHNICITY reference domain).",
    example = "W1",
  )
  val ethnicity: String? = null,
)
