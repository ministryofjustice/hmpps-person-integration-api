package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto
import java.time.LocalDate

@Schema(description = "Core Person Record Alias - DTO for use in returning alias data for the Core Person Record proxy")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CorePersonRecordAlias(

  @Schema(description = "Prisoner number", example = "A1234AA")
  val prisonerNumber: String,

  @Schema(description = "Offender ID", example = "543548")
  val offenderId: Long,

  @Schema(description = "Boolean flag to indicate if the alias is a working name", example = "true")
  val isWorkingName: Boolean,

  @Schema(description = "First name", example = "John")
  val firstName: String,

  @Schema(description = "Middle name 1", example = "Middleone")
  val middleName1: String? = null,

  @Schema(description = "Middle name 2", example = "Middletwo")
  val middleName2: String? = null,

  @Schema(description = "Last name", example = "Smith")
  val lastName: String,

  @Schema(description = "Date of birth", example = "1980-02-28")
  val dateOfBirth: LocalDate,

  @Schema(description = "Name type")
  val nameType: CorePersonRecordReferenceDataValue? = null,

  @Schema(description = "Title")
  val title: CorePersonRecordReferenceDataValue? = null,

  @Schema(description = "Sex")
  val sex: CorePersonRecordReferenceDataValue? = null,

  @Schema(description = "Ethnicity")
  val ethnicity: CorePersonRecordReferenceDataValue? = null,
) {
  fun toResponseDto(): PseudonymResponseDto = PseudonymResponseDto(
    sourceSystemId = this.offenderId,
    sourceSystem = SourceSystem.NOMIS,
    prisonerNumber = this.prisonerNumber,
    isWorkingName = this.isWorkingName,
    firstName = this.firstName,
    middleName1 = this.middleName1,
    middleName2 = this.middleName2,
    lastName = this.lastName,
    dateOfBirth = this.dateOfBirth,
    nameType = this.nameType?.toReferenceDataValue(),
    title = this.title?.toReferenceDataValue(),
    sex = this.sex?.toReferenceDataValue(),
    ethnicity = this.ethnicity?.toReferenceDataValue(),
  )
}

data class CorePersonRecordReferenceDataValue(
  val domain: String?,
  val code: String,
  val description: String,
) {
  fun toReferenceDataValue(): ReferenceDataValue = ReferenceDataValue("${domain}_$code", code, description)
}
