package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto

@Schema(description = "Person - DTO for use in returning all person data for use in the Core Person Record proxy")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FullPersonPrisonDto(

  @Schema(description = "List of aliases for the person")
  val aliases: List<CorePersonRecordAlias>,

  @Schema(description = "List of addresses for the person")
  val addresses: List<AddressPrisonDto>, // offenderNo

  @Schema(description = "List of phone numbers for the person")
  val phones: List<PhoneNumberPrisonDto>, // offenderNo

  @Schema(description = "List of email addresses for the person")
  val emails: List<EmailAddressPrisonDto>, // offenderNo

  @Schema(description = "Military record for the person")
  val militaryRecord: MilitaryRecordPrisonDto, // optional?

  @Schema(description = "Physical attributes for the person")
  val physicalAttributes: PhysicalAttributesDto, // optional?

  @Schema(description = "List of distinguishing marks for the person")
  val distinguishingMarks: List<DistinguishingMarkPrisonDto>, // prisonerNumber

)