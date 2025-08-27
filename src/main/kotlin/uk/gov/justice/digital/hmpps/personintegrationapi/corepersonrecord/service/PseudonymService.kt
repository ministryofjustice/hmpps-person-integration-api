package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.PseudonymRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto

@Service
class PseudonymService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getPseudonyms(
    prisonerNumber: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
  ): ResponseEntity<List<PseudonymResponseDto>> {
    val response = prisonApiClient.getAliases(prisonerNumber)
    return if (response.statusCode.is2xxSuccessful) {
      ResponseEntity.ok(response.body?.map { it.toResponseDto() })
    } else {
      ResponseEntity.status(response.statusCode).build()
    }
  }

  fun createPseudonym(
    prisonerNumber: String,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
    createRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> {
    val response = prisonApiClient.createAlias(prisonerNumber, createRequest.toCreateAlias())
    return if (response.statusCode.is2xxSuccessful) {
      ResponseEntity.status(HttpStatus.CREATED).body(response.body?.toResponseDto())
    } else {
      ResponseEntity.status(response.statusCode).build()
    }
  }

  fun updatePseudonym(
    pseudonymId: Long,
    sourceSystem: String = "REMOVE_ME_FOR_V2",
    updateRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> {
    val response = prisonApiClient.updateAlias(pseudonymId, updateRequest.toUpdateAlias())
    return if (response.statusCode.is2xxSuccessful) {
      ResponseEntity.ok(response.body?.toResponseDto())
    } else {
      ResponseEntity.status(response.statusCode).build()
    }
  }

  private fun CorePersonRecordAlias.toResponseDto(): PseudonymResponseDto = PseudonymResponseDto(
    sourceSystemId = offenderId,
    sourceSystem = NOMIS,
    prisonerNumber = prisonerNumber,
    isWorkingName = isWorkingName,
    firstName = firstName,
    middleName1 = middleName1,
    middleName2 = middleName2,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    nameType = nameType?.toReferenceDataValue(),
    title = title?.toReferenceDataValue(),
    sex = sex?.toReferenceDataValue(),
    ethnicity = ethnicity?.toReferenceDataValue(),
  )

  private fun PseudonymRequestDto.toUpdateAlias() = UpdateAlias(
    firstName = firstName,
    middleName1 = middleName1,
    middleName2 = middleName2,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    nameType = nameType,
    title = title,
    sex = sex,
    ethnicity = ethnicity,
  )

  private fun PseudonymRequestDto.toCreateAlias() = CreateAlias(
    firstName = firstName,
    middleName1 = middleName1,
    middleName2 = middleName2,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    nameType = nameType,
    title = title,
    sex = sex,
    ethnicity = ethnicity,
    isWorkingName = isWorkingName,
  )

  private fun CorePersonRecordReferenceDataValue.toReferenceDataValue() = ReferenceDataValue(
    id = "${domain}_$code",
    code,
    description,
  )
}
