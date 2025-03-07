package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.VirusScanResult

@HttpExchange("/documents")
interface DocumentApiClient {
  @PostExchange("/scan", contentType = MULTIPART_FORM_DATA_VALUE, accept = [APPLICATION_JSON_VALUE])
  fun virusScan(
    @RequestPart(name = "file") file: MultipartFile?,
  ): ResponseEntity<VirusScanResult>
}
