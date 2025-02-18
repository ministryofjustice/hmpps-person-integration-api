package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
@Schema(description = "DTO representing a distinguishing mark with details around body part, mark type etc.")
data class DistinguishingMarkDto(
  @Schema(
    description = "The sequence number of the distinguishing mark within the booking.",
    example = "1",
  )
  val id: Int,

  @Schema(
    description = "The id of the booking that the distinguishing mark is associated with.",
    example = "1",
  )
  val bookingId: Long,

  @Schema(
    description = "Offender unique reference",
    example = "A1234AA",
  )
  val offenderNo: String? = null,

  @Schema(
    description = "The body part that the mark is on",
    example = "TORSO",
  )
  val bodyPart: String? = null,

  @Schema(
    description = "The type of distinguishign mark (e.g. scar, tattoo)",
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

  @Schema(description = "The date and time the data was created")
  val createdAt: LocalDateTime? = null,

  @Schema(
    description = "The username of the user that created this distinguishing mark.",
    example = "TORSO",
  )
  val createdBy: String? = null,

  @Schema(description = "List of details of images associated with this distinguishing mark.")
  val photographUuids: List<DistinguishingMarkImageDetail> = listOf(),
)

data class DistinguishingMarkImageDetail(
  @Schema(description = "The image id")
  val id: Long? = null,

  @Schema(description = "True if this image is the latest one associated with a mark")
  val latest: Boolean = false,
)
