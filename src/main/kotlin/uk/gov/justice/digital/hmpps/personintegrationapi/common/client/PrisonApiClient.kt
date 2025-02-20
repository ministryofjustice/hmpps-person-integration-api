package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateReligion
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto

@HttpExchange("/api")
interface PrisonApiClient {
  @PutExchange("/offenders/{offenderNo}/birth-place")
  fun updateBirthPlaceForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateBirthPlace: UpdateBirthPlace,
  ): ResponseEntity<Void>

  @PutExchange("/offenders/{offenderNo}/birth-country")
  fun updateBirthCountryForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateBirthCountry: UpdateBirthCountry,
  ): ResponseEntity<Void>

  @PutExchange("/offenders/{offenderNo}/nationality")
  fun updateNationalityForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateNationality: UpdateNationality,
  ): ResponseEntity<Void>

  @GetExchange("/offenders/{offenderNo}/military-records")
  fun getMilitaryRecords(
    @PathVariable offenderNo: String,
  ): ResponseEntity<MilitaryRecordPrisonDto>

  @PutExchange("/offenders/{offenderNo}/military-records/{militarySeq}")
  fun updateMilitaryRecord(
    @PathVariable offenderNo: String,
    @PathVariable militarySeq: Int,
    @RequestBody militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void>

  @PostExchange("/offenders/{offenderNo}/military-records")
  fun createMilitaryRecord(
    @PathVariable offenderNo: String,
    @RequestBody militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void>

  @PutExchange("/offenders/{offenderNo}/religion")
  fun updateReligionForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateNationality: UpdateReligion,
  ): ResponseEntity<Void>

  @GetExchange("/person/{prisonerNumber}/distinguishing-marks")
  fun getDistinguishingMarks(
    @PathVariable prisonerNumber: String,
  ): ResponseEntity<List<DistinguishingMarkDto>>

  @GetExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}")
  fun getDistinguishingMark(
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkDto>

  @PutExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}")
  fun updateDistinguishingMark(
    @RequestBody request: DistinguishingMarkUpdateRequest,
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkDto>

  @PostExchange("/person/{prisonerNumber}/distinguishing-mark", accept = [APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE])
  fun createDistinguishingMark(
    @RequestPart(name = "file") file: MultipartFile?,
    @ModelAttribute request: DistinguishingMarkCreateRequest,
    @PathVariable prisonerNumber: String,
  ): ResponseEntity<DistinguishingMarkDto>

  @GetExchange("/person/photo/{imageId}")
  fun getDistinguishingMarkImage(
    @PathVariable imageId: Int,
  ): ResponseEntity<ByteArray>

  @PostExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}/photo")
  fun addDistinguishingMarkImage(
    @RequestPart(name = "file") file: MultipartFile,
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkDto>
}
