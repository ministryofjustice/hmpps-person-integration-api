package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Distinguishing mark request object. Used to update a distinguishing mark.")
data class DistinguishingMarkUpdateRequest(

  @Schema(
    description = "The body part that the mark is on",
    example = "TORSO",
  )
  val bodyPart: String? = null,

  @Schema(
    description = "The type of distinguishing mark (e.g. scar, tattoo)",
    example = "SCAR",
  )
  val markType: String? = null,

  @Schema(
    description = "The side of the body the mark is on",
    example = "L",
  )
  val side: String? = null,

  @Schema(
    description = "The orientation of the mark on the body part (e.g. upper, lower)",
    example = "UPP",
  )
  val partOrientation: String? = null,

  @Schema(description = "Comments about the distinguishing mark")
  val comment: String? = null,
)
