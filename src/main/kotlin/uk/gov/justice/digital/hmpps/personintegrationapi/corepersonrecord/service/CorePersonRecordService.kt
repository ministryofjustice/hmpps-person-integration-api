package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.NationalityUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException

@Service
class CorePersonRecordService(
  private val prisonApiClient: PrisonApiClient,
) {

  fun updateCorePersonRecordField(prisonerNumber: String, updateRequestDto: CorePersonRecordV1UpdateRequestDto) {
    when (updateRequestDto) {
      is BirthplaceUpdateDto -> prisonApiClient.updateBirthPlaceForWorkingName(prisonerNumber, UpdateBirthPlace(updateRequestDto.value))
      is NationalityUpdateDto -> prisonApiClient.updateNationalityForWorkingName(prisonerNumber, UpdateNationality(updateRequestDto.value))
      else -> throw UnknownCorePersonFieldException("Field '${updateRequestDto.fieldName}' cannot be updated.")
    }
  }
}
