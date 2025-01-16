package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.NationalityUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException

@Service
class CorePersonRecordService(
  private val prisonApiClient: PrisonApiClient,
  private val referenceDataClient: ReferenceDataClient,
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

      is NationalityUpdateDto -> prisonApiClient.updateNationalityForWorkingName(
        prisonerNumber,
        UpdateNationality(updateRequestDto.value),
      )

      else -> throw UnknownCorePersonFieldException("Field '${updateRequestDto.fieldName}' cannot be updated.")
    }
  }

  fun getReferenceDataCodes(domain: String): ResponseEntity<List<ReferenceDataCodeDto>> {
    val response = referenceDataClient.getReferenceDataByDomain(domain)

    if (response.statusCode.is2xxSuccessful) {
      val mappedResponse = response.body?.map {
        ReferenceDataCodeDto(
          "${domain}_${it.code}",
          it.code,
          it.description,
          it.listSeq,
          it.activeFlag == "Y",
        )
      }
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
        it.copy(
          militaryRankDescription = it.militaryRankDescription?.replace(rankSuffixPattern, "")?.trim(),
          )
        MilitaryRecordDto(
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
}
