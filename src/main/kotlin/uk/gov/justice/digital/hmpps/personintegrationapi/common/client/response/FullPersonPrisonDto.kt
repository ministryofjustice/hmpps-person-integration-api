package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Person - DTO for use in returning all person data for use in the Core Person Record proxy")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FullPersonPrisonDto(

  @Schema(description = "List of aliases for the person")
  val aliases: List<CorePersonRecordAlias>,

  @Schema(description = "List of addresses for the person")
  val addresses: List<AddressPrisonDto>,

  @Schema(description = "List of phone numbers for the person")
  val phones: List<PhoneNumberPrisonDto>,

  @Schema(description = "List of email addresses for the person")
  val emails: List<EmailAddressPrisonDto>,

  @Schema(description = "Military record for the person")
  val militaryRecord: MilitaryRecordPrisonDto,

  @Schema(description = "Physical attributes for the person")
  val physicalAttributes: PhysicalAttributesPrisonDto,

  @Schema(description = "List of distinguishing marks for the person")
  val distinguishingMarks: List<DistinguishingMarkPrisonDto>,

)