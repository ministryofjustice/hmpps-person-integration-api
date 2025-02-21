package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

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
)

data class DistinguishingMarkImageDetailPrisonDto(
  val id: Long,
  val latest: Boolean = false,
)
