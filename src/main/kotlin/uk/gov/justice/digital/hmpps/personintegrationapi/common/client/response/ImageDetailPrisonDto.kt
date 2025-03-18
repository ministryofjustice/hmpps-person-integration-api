package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import java.time.LocalDate
import java.time.LocalDateTime

data class ImageDetailPrisonDto(
  val imageId: Number,
  val active: Boolean,
  val captureDate: LocalDate,
  val captureDateTime: LocalDateTime,
  val imageView: String,
  val imageOrientation: String,
  val imageType: String,
  val objectId: Number?,
)
