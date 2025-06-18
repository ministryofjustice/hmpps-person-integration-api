package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateSexualOrientation
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.IdentifierPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper.mapRefDataDescription
import uk.gov.justice.digital.hmpps.personintegrationapi.common.util.virusScan
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.CreateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.UpdateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.SexualOrientationUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.DuplicateIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.IdentifierNotFoundException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierTypeException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers.CROIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers.PNCIdentifier

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

      is SexualOrientationUpdateDto -> prisonApiClient.updateSexualOrientationForWorkingName(
        prisonerNumber,
        UpdateSexualOrientation(updateRequestDto.value),
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

  fun updateMilitaryRecord(
    prisonerNumber: String,
    militarySeq: Int,
    militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void> = prisonApiClient.updateMilitaryRecord(prisonerNumber, militarySeq, militaryRecordRequest)

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

  fun updatePhysicalAttributes(
    prisonerNumber: String,
    physicalAttributesRequest: PhysicalAttributesRequest,
  ): ResponseEntity<Void> = prisonApiClient.updatePhysicalAttributes(prisonerNumber, physicalAttributesRequest)

  fun updateProfileImage(file: MultipartFile, prisonerNumber: String): ResponseEntity<Void> {
    virusScan(file, documentApiClient)
    val response = prisonApiClient.updateProfileImage(file, prisonerNumber)
    if (response.statusCode.isError) {
      return ResponseEntity.status(response.statusCode).build()
    }
    return ResponseEntity.noContent().build()
  }

  fun addIdentifiers(prisonerNumber: String, createRequests: List<CreateIdentifierRequestDto>): ResponseEntity<Void> {
    val idTypesResponse = referenceDataClient.getReferenceDataByDomain(ID_TYPE)
    if (idTypesResponse.statusCode.isError) {
      return ResponseEntity.status(idTypesResponse.statusCode).build()
    }
    val activeIdTypes = idTypesResponse.body
      ?.filter { it.activeFlag == "Y" }
      ?.map { it.code }
      ?: emptyList()

    val identifiersResponse = prisonApiClient.getAllIdentifiers(prisonerNumber)
    if (identifiersResponse.statusCode.isError) {
      return ResponseEntity.status(identifiersResponse.statusCode).build()
    }
    val existingIdentifiers = identifiersResponse.body
      ?.groupBy(keySelector = { it.type }, valueTransform = { convertIdToCanonicalForm(it.value, it.type) })
      ?: mapOf()

    val mappedRequests = createRequests.map {
      CreateIdentifier(
        identifierType = it.type,
        identifier = convertIdToCanonicalForm(it.value, it.type),
        issuedAuthorityText = it.comments,
      )
    }.onEach {
      it.validate(prisonerNumber, existingIdentifiers, activeIdTypes)
    }

    val response = prisonApiClient.addIdentifiers(
      prisonerNumber,
      mappedRequests,
    )
    return ResponseEntity.status(response.statusCode).build()
  }

  fun updateIdentifier(
    offenderId: Long,
    id: Long,
    updateRequest: UpdateIdentifierRequestDto,
  ): ResponseEntity<Void> {
    val aliasResponse = prisonApiClient.getAlias(offenderId)
    if (aliasResponse.statusCode.isError) {
      return ResponseEntity.status(aliasResponse.statusCode).build()
    }

    val prisonerNumber = aliasResponse.body?.prisonerNumber ?: throw IdentifierNotFoundException(offenderId, id)
    val identifiersResponse = prisonApiClient.getAllIdentifiers(prisonerNumber)
    if (identifiersResponse.statusCode.isError) {
      return ResponseEntity.status(identifiersResponse.statusCode).build()
    }

    val identifierType = identifiersResponse.body
      ?.firstOrNull { it.offenderIdSeq == id }
      ?.type
      ?: throw IdentifierNotFoundException(offenderId, id)

    val request = UpdateIdentifier(
      identifier = convertIdToCanonicalForm(updateRequest.value, identifierType),
      issuedAuthorityText = updateRequest.comments,
    ).also { it.validate(prisonerNumber, identifierType, id, identifiersResponse.body) }

    val response = prisonApiClient.updateIdentifier(
      offenderId,
      id,
      request,
    )
    return ResponseEntity.status(response.statusCode).build()
  }

  private fun convertIdToCanonicalForm(value: String, type: String): String = when (type) {
    "PNC" -> PNCIdentifier.from(value).pncId
    "CRO" -> CROIdentifier.from(value).croId
    else -> value
  }

  private fun CreateIdentifier.validate(prisonerNumber: String, existingIdentifiers: Map<String, List<String>>, activeTypes: List<String>) {
    if (!activeTypes.contains(this.identifierType)) {
      throw InvalidIdentifierTypeException(this.identifierType)
    }

    if (this.identifier.isEmpty()) {
      throw InvalidIdentifierException(this.identifierType)
    }

    if (
      existingIdentifiers.containsKey(this.identifierType) &&
      existingIdentifiers[this.identifierType]?.contains(this.identifier) == true
    ) {
      throw DuplicateIdentifierException(prisonerNumber, this.identifierType)
    }
  }

  private fun UpdateIdentifier.validate(
    prisonerNumber: String,
    type: String,
    id: Long,
    existingIdentifiers: List<IdentifierPrisonDto>?,
  ) {
    if (this.identifier.isEmpty()) {
      throw InvalidIdentifierException(type)
    }

    if (existingIdentifiers
        ?.filter { it.type == type && it.offenderIdSeq != id }
        ?.map { convertIdToCanonicalForm(it.value, type) }
        ?.any { it == this.identifier } == true
    ) {
      throw DuplicateIdentifierException(prisonerNumber, type)
    }
  }

  companion object {
    val excludedCodes = setOf(
      Pair("FACIAL_HAIR", "NA"),
      Pair("L_EYE_C", "MISSING"),
      Pair("R_EYE_C", "MISSING"),
    )

    const val ID_TYPE = "ID_TYPE"
  }
}
