package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.FullPersonResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto

@Service
class PersonService(
  private val prisonApiClient: PrisonApiClient,
) {
  fun getPerson(prisonerNumber: String): ResponseEntity<FullPersonResponseDto> {
    val response = prisonApiClient.getFullPerson(prisonerNumber)
    val body = response.body ?: return ResponseEntity.status(response.statusCode).build()

    if (!response.statusCode.is2xxSuccessful) {
      return ResponseEntity.status(response.statusCode).build()
    }

    val contacts = buildList {
      body.phones.forEach { phone ->
        add(ContactResponseDto(phone.phoneId, phone.type, phone.number, phone.ext))
      }
      body.emails.forEach { email ->
        add(ContactResponseDto(email.emailAddressId, "EMAIL", email.email))
      }
    }

    // Military
    val rankSuffixList = setOf("\\(Army\\)", "\\(Navy\\)", "\\(RAF\\)", "\\(Royal Marines\\)")
    val rankSuffixPattern = Regex(rankSuffixList.joinToString("|"), RegexOption.IGNORE_CASE)
    val militaryRecords = body.militaryRecord.militaryRecords.map {
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

    //Phys att
    val physicalAttributes =
      PhysicalAttributesDto(
        height = body.physicalAttributes.height,
        weight = body.physicalAttributes.weight,
        hair = body.physicalAttributes.hair?.toReferenceDataValue(),
        facialHair = body.physicalAttributes.facialHair?.toReferenceDataValue(),
        face = body.physicalAttributes.face?.toReferenceDataValue(),
        build = body.physicalAttributes.build?.toReferenceDataValue(),
        leftEyeColour = body.physicalAttributes.leftEyeColour?.toReferenceDataValue(),
        rightEyeColour = body.physicalAttributes.rightEyeColour?.toReferenceDataValue(),
        shoeSize = body.physicalAttributes.shoeSize,
      )

    val mappedResponse = FullPersonResponseDto(
      addresses = body.addresses.map { it.toAddressResponseDto(prisonerNumber) },
      pseudonyms = body.aliases.map { it.toResponseDto() },
      contacts = contacts.toMutableList(),
      militaryRecords = militaryRecords,
      physicalAttributes = physicalAttributes,
      distinguishingMarks = body.distinguishingMarks.map { it.toDto() },
    )

    return ResponseEntity.ok(mappedResponse)
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

  private fun CorePersonRecordReferenceDataValue.toReferenceDataValue() = ReferenceDataValue(
    id = "${domain}_$code",
    code,
    description,
  )

  private fun DistinguishingMarkPrisonDto.toDto(): DistinguishingMarkDto = DistinguishingMarkDto(
    id = id,
    bookingId = bookingId,
    offenderNo = offenderNo,
    bodyPart = bodyPart?.toReferenceDataValue(),
    markType = markType?.toReferenceDataValue(),
    side = side?.toReferenceDataValue(),
    partOrientation = partOrientation?.toReferenceDataValue(),
    comment = comment,
    createdAt = createdAt,
    createdBy = createdBy,
    photographUuids = photographUuids.map { DistinguishingMarkImageDetail(it.id, it.latest) },
  )

}

