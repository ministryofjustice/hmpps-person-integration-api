package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
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
}
