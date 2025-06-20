package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Contact response object containing phone number or email contact details.")
data class ContactResponseDto(

  @Schema(
    description = "The ID of the contact the reference is associated with. " +
      "While NOMIS is the data source the contact ID will be either be the phone number ID or the email ID based on the type.",
    example = "true",
  )
  @field:NotNull
  val contactId: Long,

  @Schema(
    description = "Contact type",
    example = "HOME",
    allowableValues = ["HOME", "BUS", "FAX", "ALTB", "ALTH", "MOB", "VISIT", "EMAIL"],
  )
  @field:NotNull
  val contactType: String,

  @Schema(
    description = "Contact value. For contacts with type EMAIL this will have a maximum length of 240 characters." +
      "For contacts of any other type these will be treated as phone numbers with a maximum length of 40 characters.",
    example = "01234 567 789",
  )
  @field:Size(max = 240)
  @field:NotNull
  val contactValue: String,

  @Schema(
    description = "Contact phone extension. For contacts with type EMAIL this has no effect." +
      "For contacts of any other type this has a maximum length of 7 characters.",
    example = "123",
  )
  @field:Size(max = 7)
  val contactPhoneExtension: String? = null,
)
