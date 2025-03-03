package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue

@JsonInclude(NON_NULL)
@Schema(description = "Physical Attributes")
data class PhysicalAttributesDto(
  @Schema(description = "Height (in centimetres)")
  val height: Int? = null,

  @Schema(description = "Weight (in kilograms)")
  val weight: Int? = null,

  @Schema(description = "Hair type or colour")
  val hair: ReferenceDataValue? = null,

  @Schema(description = "Facial hair type")
  val facialHair: ReferenceDataValue? = null,

  @Schema(description = "Face shape")
  val face: ReferenceDataValue? = null,

  @Schema(description = "Build")
  val build: ReferenceDataValue? = null,

  @Schema(description = "Left eye colour")
  val leftEyeColour: ReferenceDataValue? = null,

  @Schema(description = "Right eye colour")
  val rightEyeColour: ReferenceDataValue? = null,

  @Schema(description = "Shoe size", example = "9")
  val shoeSize: String? = null,
)
