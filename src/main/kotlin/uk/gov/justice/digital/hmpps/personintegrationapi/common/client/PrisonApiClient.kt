package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateReligion
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto

@HttpExchange("/api/offenders")
interface PrisonApiClient {
  @PutExchange("/{offenderNo}/birth-place")
  fun updateBirthPlaceForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateBirthPlace: UpdateBirthPlace,
  ): ResponseEntity<Void>

  @PutExchange("/{offenderNo}/birth-country")
  fun updateBirthCountryForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateBirthCountry: UpdateBirthCountry,
  ): ResponseEntity<Void>

  @PutExchange("/{offenderNo}/nationality")
  fun updateNationalityForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateNationality: UpdateNationality,
  ): ResponseEntity<Void>

  @GetExchange("/{offenderNo}/military-records")
  fun getMilitaryRecords(
    @PathVariable offenderNo: String,
  ): ResponseEntity<MilitaryRecordPrisonDto>

  @PutExchange("/{offenderNo}/military-records/{militarySeq}")
  fun updateMilitaryRecord(
    @PathVariable offenderNo: String,
    @PathVariable militarySeq: Int,
    @RequestBody militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void>

  @PostExchange("/{offenderNo}/military-records")
  fun createMilitaryRecord(
    @PathVariable offenderNo: String,
    @RequestBody militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void>

  @PutExchange("/{offenderNo}/religion")
  fun updateReligionForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateNationality: UpdateReligion,
  ): ResponseEntity<Void>
}
