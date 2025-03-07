package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

data class VirusScanResult(
  val status: VirusScanStatus,
  val result: String?,
)

enum class VirusScanStatus {
  PASSED,
  FAILED,
  ERROR,
}
