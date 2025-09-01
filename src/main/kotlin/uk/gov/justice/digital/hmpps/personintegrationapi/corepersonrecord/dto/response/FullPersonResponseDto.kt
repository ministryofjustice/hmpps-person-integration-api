package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO representing the full details of a person")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FullPersonResponseDto(

  @Schema(description = "List of pseudonyms for the person")
  val pseudonyms: List<PseudonymResponseDto>,

  @Schema(description = "List of addresses for the person")
  val addresses: List<AddressResponseDto>,

  @Schema(description = "List of contact details for the person")
  val contacts: List<ContactResponseDto>,

  @Schema(description = "List of military records for the person")
  val militaryRecords: List<MilitaryRecordDto>,

  @Schema(description = "Physical attributes for the person")
  val physicalAttributes: PhysicalAttributesDto,

  @Schema(description = "List of distinguishing marks for the person")
  val distinguishingMarks: List<DistinguishingMarkDto>,
)
