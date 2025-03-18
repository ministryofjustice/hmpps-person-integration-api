package uk.gov.justice.digital.hmpps.personintegrationapi.common.util

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanResult
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanStatus
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanFailureException

fun virusScan(file: MultipartFile?, documentApiClient: DocumentApiClient) {
  if (file != null) {
    try {
      val scanResult = documentApiClient.virusScan(file)

      if (scanResult.statusCode.is2xxSuccessful) {
        val result = scanResult.body as VirusScanResult
        when (result.status) {
          VirusScanStatus.FAILED -> throw VirusScanFailureException("Virus scan failed - ${result.result}")
          VirusScanStatus.ERROR -> throw VirusScanFailureException("Virus scan error - ${result.result}")
          VirusScanStatus.PASSED -> {}
        }
      } else {
        throw VirusScanException()
      }
    } catch (e: InternalServerError) {
      throw VirusScanException()
    }
  }
}
