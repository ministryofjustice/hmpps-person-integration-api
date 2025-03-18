package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper.mapRefDataDescription
import uk.gov.justice.digital.hmpps.personintegrationapi.common.util.virusScan
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException

@Service
class CorePersonRecordService(
  private val prisonApiClient: PrisonApiClient,
  private val referenceDataClient: ReferenceDataClient,
  private val documentApiClient: DocumentApiClient,
) {

  fun updateCorePersonRecordField(prisonerNumber: String, updateRequestDto: CorePersonRecordV1UpdateRequestDto) {
    when (updateRequestDto) {
      is BirthplaceUpdateDto -> prisonApiClient.updateBirthPlaceForWorkingName(
        prisonerNumber,
        UpdateBirthPlace(updateRequestDto.value),
      )

      is CountryOfBirthUpdateDto -> prisonApiClient.updateBirthCountryForWorkingName(
        prisonerNumber,
        UpdateBirthCountry(updateRequestDto.value),
      )

      else -> throw UnknownCorePersonFieldException("Field '${updateRequestDto.fieldName}' cannot be updated.")
    }
  }

  fun getReferenceDataCodes(domain: String): ResponseEntity<List<ReferenceDataCodeDto>> {
    val response = referenceDataClient.getReferenceDataByDomain(domain)

    if (response.statusCode.is2xxSuccessful) {
      val mappedResponse = response.body
        ?.filterNot { excludedCodes.contains(Pair(it.domain, it.code)) }
        ?.map {
          ReferenceDataCodeDto(
            "${domain}_${it.code}",
            it.code,
            mapRefDataDescription(it.domain, it.code, it.description),
            it.listSeq,
            it.activeFlag == "Y",
            it.parentCode,
            it.parentDomain,
          )
        }
        ?.sortedBy { it.listSequence }
      return ResponseEntity.ok(mappedResponse)
    } else {
      return ResponseEntity.status(response.statusCode).build()
    }
  }

  fun getMilitaryRecords(prisonerNumber: String): ResponseEntity<List<MilitaryRecordDto>> {
    val response = prisonApiClient.getMilitaryRecords(prisonerNumber)

    if (response.statusCode.is2xxSuccessful) {
      val rankSuffixList = setOf("\\(Army\\)", "\\(Navy\\)", "\\(RAF\\)", "\\(Royal Marines\\)")
      val rankSuffixPattern = Regex(rankSuffixList.joinToString("|"), RegexOption.IGNORE_CASE)

      val mappedResponse = response.body?.militaryRecords?.map {
        MilitaryRecordDto(
          militarySeq = it.militarySeq,
          warZoneCode = it.warZoneCode,
          warZoneDescription = it.warZoneDescription,
          startDate = it.startDate,
          endDate = it.endDate,
          militaryDischargeCode = it.militaryDischargeCode,
          militaryDischargeDescription = it.militaryDischargeDescription,
          militaryBranchCode = it.militaryBranchCode,
          militaryBranchDescription = it.militaryBranchDescription,
          description = it.description,
          unitNumber = it.unitNumber,
          enlistmentLocation = it.enlistmentLocation,
          dischargeLocation = it.dischargeLocation,
          selectiveServicesFlag = it.selectiveServicesFlag,
          militaryRankCode = it.militaryRankCode,
          militaryRankDescription = it.militaryRankDescription?.replace(rankSuffixPattern, "")?.trim(),
          serviceNumber = it.serviceNumber,
          disciplinaryActionCode = it.disciplinaryActionCode,
          disciplinaryActionDescription = it.disciplinaryActionDescription,
        )
      }
      return ResponseEntity.ok(mappedResponse)
    } else {
      return ResponseEntity.status(response.statusCode).build()
    }
  }

  fun updateMilitaryRecord(prisonerNumber: String, militarySeq: Int, militaryRecordRequest: MilitaryRecordRequest): ResponseEntity<Void> = prisonApiClient.updateMilitaryRecord(prisonerNumber, militarySeq, militaryRecordRequest)

  fun createMilitaryRecord(prisonerNumber: String, militaryRecordRequest: MilitaryRecordRequest): ResponseEntity<Void> = prisonApiClient.createMilitaryRecord(prisonerNumber, militaryRecordRequest)

  fun updateNationality(prisonerNumber: String, updateNationality: UpdateNationality): ResponseEntity<Void> = prisonApiClient.updateNationalityForWorkingName(prisonerNumber, updateNationality)

  fun getPhysicalAttributes(prisonerNumber: String): ResponseEntity<PhysicalAttributesDto> {
    val response = prisonApiClient.getPhysicalAttributes(prisonerNumber)

    if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

    val mappedResponse = response.body?.let { body ->
      PhysicalAttributesDto(
        height = body.height,
        weight = body.weight,
        hair = body.hair?.toReferenceDataValue(),
        facialHair = body.facialHair?.toReferenceDataValue(),
        face = body.face?.toReferenceDataValue(),
        build = body.build?.toReferenceDataValue(),
        leftEyeColour = body.leftEyeColour?.toReferenceDataValue(),
        rightEyeColour = body.rightEyeColour?.toReferenceDataValue(),
        shoeSize = body.shoeSize,
      )
    }

    return ResponseEntity.ok(mappedResponse)
  }

  fun updatePhysicalAttributes(prisonerNumber: String, physicalAttributesRequest: PhysicalAttributesRequest): ResponseEntity<Void> = prisonApiClient.updatePhysicalAttributes(prisonerNumber, physicalAttributesRequest)

  fun updateProfileImage(file: MultipartFile, prisonerNumber: String): ResponseEntity<Void> {
    virusScan(file, documentApiClient)
    val response = prisonApiClient.updateProfileImage(file, prisonerNumber)
    if (response.statusCode.isError) {
      return ResponseEntity.status(response.statusCode).build()
    }
    return ResponseEntity.noContent().build()
  }

  companion object {
    val excludedCodes = setOf(
      Pair("FACIAL_HAIR", "NA"),
      Pair("L_EYE_C", "MISSING"),
      Pair("R_EYE_C", "MISSING"),
    )

    const val HAIR = "HAIR"
    const val FACIAL_HAIR = "FACIAL_HAIR"
    const val FACE = "FACE"
    const val BUILD = "BUILD"
    const val L_EYE_C = "L_EYE_C"
    const val R_EYE_C = "R_EYE_C"
  }
}
