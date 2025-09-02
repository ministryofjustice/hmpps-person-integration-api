package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import java.time.LocalDateTime

data class DistinguishingMarkPrisonDto(
  val id: Int,
  val bookingId: Long,
  val offenderNo: String,
  val bodyPart: ReferenceDataCode? = null,
  val markType: ReferenceDataCode? = null,
  val side: ReferenceDataCode? = null,
  val partOrientation: ReferenceDataCode? = null,
  val comment: String? = null,
  val createdAt: LocalDateTime? = null,
  val createdBy: String? = null,
  val photographUuids: List<DistinguishingMarkImageDetailPrisonDto> = listOf(),
) {
  fun toResponseDto(): DistinguishingMarkDto = DistinguishingMarkDto(
    id = this.id,
    bookingId = this.bookingId,
    offenderNo = this.offenderNo,
    bodyPart = this.bodyPart?.toReferenceDataValue(),
    markType = this.markType?.toReferenceDataValue(),
    side = this.side?.toReferenceDataValue(),
    partOrientation = this.partOrientation?.toReferenceDataValue(),
    comment = this.comment,
    createdAt = this.createdAt,
    createdBy = this.createdBy,
    photographUuids = this.photographUuids.map { DistinguishingMarkImageDetail(it.id, it.latest) },
  )
}

data class DistinguishingMarkImageDetailPrisonDto(
  val id: Long,
  val latest: Boolean = false,
)
