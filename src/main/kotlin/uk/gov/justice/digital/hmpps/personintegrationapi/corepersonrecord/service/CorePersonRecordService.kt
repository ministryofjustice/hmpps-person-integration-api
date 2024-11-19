package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.dto.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.enumeration.CorePersonRecordField
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException

@Service
class CorePersonRecordService(
  private val prisonApiClient: PrisonApiClient,
) {

  fun updateCorePersonRecordField(prisonerNumber: String, field: CorePersonRecordField, value: String) {
    when (field) {
      CorePersonRecordField.BIRTHPLACE -> prisonApiClient.updateBirthPlaceForWorkingName(prisonerNumber, UpdateBirthPlace(value))
      else -> throw UnknownCorePersonFieldException("Field '$field' cannot be updated.")
    }
  }
}
