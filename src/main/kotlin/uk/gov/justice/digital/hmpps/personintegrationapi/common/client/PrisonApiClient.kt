package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PutExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthPlace

@HttpExchange("/api/offenders")
interface PrisonApiClient {
  @PutExchange("/{offenderNo}/birth-place")
  fun updateBirthPlaceForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateBirthPlace: UpdateBirthPlace,
  ): ResponseEntity<Void>
}
