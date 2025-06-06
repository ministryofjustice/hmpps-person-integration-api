package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

data class PhoneNumberPrisonDto(
  val phoneId: Long,
  val number: String,
  val type: String,
  val extension: String? = null,
)
