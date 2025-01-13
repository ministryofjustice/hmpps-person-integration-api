package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateReligion
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request.ReligionV1RequestDto

@Service
class PersonProtectedCharacteristicsService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun updateReligion(prisonerNumber: String, updateRequest: ReligionV1RequestDto) {
    prisonApiClient.updateReligionForWorkingName(
      prisonerNumber,
      UpdateReligion(
        updateRequest.religionCode,
        updateRequest.reasonForChange,
        updateRequest.effectiveFromDate,
        updateRequest.isVerified,
      ),
    )
  }
}
