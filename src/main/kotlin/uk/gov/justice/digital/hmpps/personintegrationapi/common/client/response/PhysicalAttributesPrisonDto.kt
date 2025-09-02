package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto

@JsonInclude(NON_NULL)
@Schema(description = "Physical Attributes")
data class PhysicalAttributesPrisonDto(
  @Schema(description = "Height (in centimetres)")
  val height: Int? = null,

  @Schema(description = "Weight (in kilograms)")
  val weight: Int? = null,

  @Schema(description = "Hair type or colour")
  val hair: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Facial hair type")
  val facialHair: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Face shape")
  val face: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Build")
  val build: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Left eye colour")
  val leftEyeColour: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Right eye colour")
  val rightEyeColour: ReferenceDataValuePrisonDto? = null,

  @Schema(description = "Shoe size", example = "9")
  val shoeSize: String? = null,
) {
  fun toResponseDto(): PhysicalAttributesDto =
    PhysicalAttributesDto(
      height = this.height,
      weight = this.weight,
      hair = this.hair?.toReferenceDataValue(),
      facialHair = this.facialHair?.toReferenceDataValue(),
      face = this.face?.toReferenceDataValue(),
      build = this.build?.toReferenceDataValue(),
      leftEyeColour = this.leftEyeColour?.toReferenceDataValue(),
      rightEyeColour = this.rightEyeColour?.toReferenceDataValue(),
      shoeSize = this.shoeSize,
    )
}
