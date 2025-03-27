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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateReligion
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ImageDetailPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhysicalAttributesPrisonDto

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

  @GetExchange("/offenders/{offenderNo}/core-person-record/physical-attributes")
  fun getPhysicalAttributes(
    @PathVariable offenderNo: String,
  ): ResponseEntity<PhysicalAttributesPrisonDto>

  @PutExchange("/offenders/{offenderNo}/core-person-record/physical-attributes")
  fun updatePhysicalAttributes(
    @PathVariable offenderNo: String,
    @RequestBody physicalAttributes: PhysicalAttributesRequest,
  ): ResponseEntity<Void>

  @GetExchange("/person/{prisonerNumber}/distinguishing-marks")
  fun getDistinguishingMarks(
    @PathVariable prisonerNumber: String,
  ): ResponseEntity<List<DistinguishingMarkPrisonDto>>

  @GetExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}")
  fun getDistinguishingMark(
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkPrisonDto>

  @PutExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}")
  fun updateDistinguishingMark(
    @RequestBody request: DistinguishingMarkUpdateRequest,
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkPrisonDto>

  @PostExchange("/person/{prisonerNumber}/distinguishing-mark", accept = [APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE])
  fun createDistinguishingMark(
    @RequestPart(name = "file") file: MultipartFile?,
    @ModelAttribute request: DistinguishingMarkCreateRequest,
    @PathVariable prisonerNumber: String,
  ): ResponseEntity<DistinguishingMarkPrisonDto>

  @GetExchange("/person/photo/{imageId}")
  fun getDistinguishingMarkImage(
    @PathVariable imageId: Long,
  ): ResponseEntity<ByteArray>

  @PutExchange("/person/photo/{imageId}/image")
  fun updateDistinguishingMarkImage(
    @RequestPart(name = "file") file: MultipartFile,
    @PathVariable imageId: Long,
  ): ResponseEntity<ByteArray>

  @PostExchange("/person/{prisonerNumber}/distinguishing-mark/{markId}/photo")
  fun addDistinguishingMarkImage(
    @RequestPart(name = "file") file: MultipartFile,
    @PathVariable prisonerNumber: String,
    @PathVariable markId: Int,
  ): ResponseEntity<DistinguishingMarkPrisonDto>

  @GetExchange("/offenders/{offenderNo}/aliases")
  fun getAliases(
    @PathVariable offenderNo: String,
  ): ResponseEntity<List<CorePersonRecordAlias>>

  @PutExchange("/aliases/{offenderId}")
  fun updateAlias(
    @PathVariable offenderId: Long,
    @RequestBody request: UpdateAlias,
  ): ResponseEntity<CorePersonRecordAlias>

  @PostExchange("/offenders/{offenderNo}/aliases")
  fun createAlias(
    @PathVariable offenderNo: String,
    @RequestBody request: CreateAlias,
  ): ResponseEntity<CorePersonRecordAlias>

  @PostExchange("/images/offenders/{prisonerNumber}")
  fun updateProfileImage(
    @RequestPart(name = "file") file: MultipartFile,
    @PathVariable prisonerNumber: String,
  ): ResponseEntity<ImageDetailPrisonDto>
}
