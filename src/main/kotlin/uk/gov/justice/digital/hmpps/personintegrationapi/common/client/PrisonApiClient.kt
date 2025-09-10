package uk.gov.justice.digital.hmpps.personintegrationapi.common.client

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateEmailAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreatePhoneNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateReligion
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateSexualOrientation
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.EmailAddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.IdentifierPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.ImageDetailPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhoneNumberPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhysicalAttributesPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PrisonerProfileSummaryPrisonDto

@HttpExchange("/api")
interface PrisonApiClient {

  @GetExchange("/offenders/{offenderNo}/profile-summary")
  fun getPrisonerProfileSummary(
    @PathVariable offenderNo: String,
  ): ResponseEntity<PrisonerProfileSummaryPrisonDto>

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

  @PutExchange("/offenders/{offenderNo}/sexual-orientation")
  fun updateSexualOrientationForWorkingName(
    @PathVariable offenderNo: String,
    @RequestBody updateSexualOrientation: UpdateSexualOrientation,
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

  @GetExchange("/aliases/{offenderId}")
  fun getAlias(
    @PathVariable offenderId: Long,
  ): ResponseEntity<CorePersonRecordAlias>

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

  @GetExchange("/offenders/{offenderNo}/phone-numbers")
  fun getPhoneNumbers(
    @PathVariable offenderNo: String,
  ): ResponseEntity<List<PhoneNumberPrisonDto>>

  @PostExchange("/offenders/{offenderNo}/phone-numbers")
  fun createPhoneNumber(
    @PathVariable offenderNo: String,
    @RequestBody request: CreatePhoneNumber,
  ): ResponseEntity<PhoneNumberPrisonDto>

  @PutExchange("/offenders/{offenderNo}/phone-numbers/{phoneNumberId}")
  fun updatePhoneNumber(
    @PathVariable offenderNo: String,
    @PathVariable phoneNumberId: Long,
    @RequestBody request: CreatePhoneNumber,
  ): ResponseEntity<PhoneNumberPrisonDto>

  @GetExchange("/offenders/{offenderNo}/email-addresses")
  fun getEmailAddresses(
    @PathVariable offenderNo: String,
  ): ResponseEntity<List<EmailAddressPrisonDto>>

  @PostExchange("/offenders/{offenderNo}/email-addresses")
  fun createEmailAddress(
    @PathVariable offenderNo: String,
    @RequestBody request: CreateEmailAddress,
  ): ResponseEntity<EmailAddressPrisonDto>

  @PutExchange("/offenders/{offenderNo}/email-addresses/{emailAddressId}")
  fun updateEmailAddress(
    @PathVariable offenderNo: String,
    @PathVariable emailAddressId: Long,
    @RequestBody request: CreateEmailAddress,
  ): ResponseEntity<EmailAddressPrisonDto>

  @GetExchange("/offenders/{offenderNo}/offender-identifiers")
  fun getAllIdentifiers(
    @PathVariable offenderNo: String,
    @RequestParam includeAliases: Boolean = true,
  ): ResponseEntity<List<IdentifierPrisonDto>>

  @PutExchange("/aliases/{offenderId}/offender-identifiers/{seqId}")
  fun updateIdentifier(
    @PathVariable offenderId: Long,
    @PathVariable seqId: Long,
    @RequestBody request: UpdateIdentifier,
  ): ResponseEntity<Void>

  @PostExchange("/offenders/{offenderNo}/offender-identifiers")
  fun addIdentifiers(
    @PathVariable offenderNo: String,
    @RequestBody request: List<CreateIdentifier>,
  ): ResponseEntity<Void>

  @GetExchange("/offenders/{offenderNo}/addresses")
  fun getAddresses(@PathVariable offenderNo: String): ResponseEntity<List<AddressPrisonDto>>

  @PostExchange("/offenders/{offenderNo}/addresses")
  fun createAddress(
    @PathVariable offenderNo: String,
    @RequestBody request: CreateAddress,
  ): ResponseEntity<AddressPrisonDto>
}
