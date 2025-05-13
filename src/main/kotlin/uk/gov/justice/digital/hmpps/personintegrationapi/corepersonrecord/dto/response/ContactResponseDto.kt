package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import java.time.LocalDate
import java.util.UUID

@Schema(description = "Contact response object containing phone number or email contact details for a person.")
data class ContactResponseDto(

  @Schema(
    description = "The ID of the person the reference is associated with. " +
      "While NOMIS is the data source the person ID will be the prisoner number.",
    example = "true",
  )
  @field:NotNull
  val personId: String,

  @Schema(
    description = "Contact type",
    example = "HOME",
    allowableValues = ["HOME", "BUS", "FAX", "ALTB", "ALTH", "MOB", "VISIT", "EMAIL"],
  )
  @field:NotNull
  val contactType: String,

  @Schema(description = "Contact value", example = "01234 567 789")
  @field:Size(max = 240)
  @field:NotNull
  val contactValue: String,
)
