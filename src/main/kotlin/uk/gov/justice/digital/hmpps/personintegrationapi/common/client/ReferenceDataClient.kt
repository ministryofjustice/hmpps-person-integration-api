package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ReferenceDataCode

@HttpExchange("/api/reference-domains")
interface ReferenceDataClient {
  @GetExchange("/domains/{domain}/all-codes")
  fun getReferenceDataByDomain(
    @PathVariable domain: String,
  ): ResponseEntity<List<ReferenceDataCode>>
}
