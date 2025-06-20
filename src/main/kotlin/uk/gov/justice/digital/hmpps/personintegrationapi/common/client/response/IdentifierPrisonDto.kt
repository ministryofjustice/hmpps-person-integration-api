package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

data class IdentifierPrisonDto(
  val type: String,
  val value: String,
  val issuedAuthorityText: String?,
  val offenderId: Long,
  val offenderIdSeq: Long,
)
