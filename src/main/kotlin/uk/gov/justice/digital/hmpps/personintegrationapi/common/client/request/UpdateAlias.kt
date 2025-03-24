package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Alias request object. Used to update an alias.")
data class UpdateAlias(
  @Schema(description = "First name", example = "John")
  val firstName: String,

  @Schema(description = "Middle name 1", example = "Middleone")
  val middleName1: String? = null,

  @Schema(description = "Middle name 2", example = "Middletwo")
  val middleName2: String? = null,

  @Schema(description = "Last name", example = "Smith")
  val lastName: String,

  @Schema(description = "Date of birth. Must be specified in YYYY-MM-DD format. Range allowed is 16-110 years")
  val dateOfBirth: LocalDate,

  @Schema(description = "The name type (from NAME_TYPE reference domain)", example = "CN")
  val nameType: String? = null,

  @Schema(description = "A code representing the person's title (from TITLE reference domain).", example = "MR")
  val title: String? = null,

  @Schema(description = "A code representing the person's sex (from SEX reference domain).", example = "F")
  val sex: String,

  @Schema(description = "A code representing the person's ethnicity (from ETHNICITY reference domain).", example = "W1")
  val ethnicity: String? = null,
)
