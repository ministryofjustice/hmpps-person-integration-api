package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem.NOMIS
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordAlias
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.CorePersonRecordReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.DistinguishingMarkPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.FullPersonPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.MilitaryRecordPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhysicalAttributesPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.AddressTypeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
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

    val mappedResponse = FullPersonResponseDto(
      addresses = body.addresses.map { it.toResponseDto(prisonerNumber) },
      pseudonyms = body.aliases.map { it.toResponseDto() },
      contacts = body.toContactsResponseDto(),
      militaryRecords = body.militaryRecord.toResponseDto(),
      physicalAttributes = body.physicalAttributes.toResponseDto(),
      distinguishingMarks = body.distinguishingMarks.map { it.toResponseDto() },
    )

    return ResponseEntity.ok(mappedResponse)
  }

  private fun AddressPrisonDto.toResponseDto(personId: String) = AddressResponseDto(
    addressId = this.addressId,
    personId = personId,
    uprn = null,
    noFixedAbode = this.noFixedAddress,
    subBuildingName = this.flat,
    buildingNumber = null,
    buildingName = this.premise,
    thoroughfareName = this.street,
    dependantLocality = this.locality,
    postTown = this.townCode?.let {
      ReferenceDataValue("CITY_$it", it, this.town!!)
    },
    county = this.countyCode?.let {
      ReferenceDataValue("COUNTY_$it", it, this.county!!)
    },
    country = this.countryCode?.let {
      ReferenceDataValue("COUNTRY_$it", it, this.country!!)
    },
    postCode = this.postalCode,
    fromDate = this.startDate,
    toDate = this.endDate,
    addressTypes = this.addressUsages.map { usage ->
      AddressTypeDto(
        active = usage.activeFlag,
        addressUsageType = ReferenceDataValue(
          "ADDRESS_TYPE_${usage.addressUsage}",
          usage.addressUsage,
          usage.addressUsageDescription,
        ),
      )
    },
    primaryAddress = this.primary,
    postalAddress = this.mail,
    comment = this.comment,
    addressPhoneNumbers = this.phones.map { phone ->
      ContactResponseDto(
        contactId = phone.phoneId,
        contactType = phone.type,
        contactValue = phone.number,
        contactPhoneExtension = phone.ext,
      )
    },
  )

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

  private fun FullPersonPrisonDto.toContactsResponseDto(): List<ContactResponseDto> {
    return buildList {
      phones.forEach { phone ->
        add(ContactResponseDto(phone.phoneId, phone.type, phone.number, phone.ext))
      }
      emails.forEach { email ->
        add(ContactResponseDto(email.emailAddressId, "EMAIL", email.email))
      }
    }
  }

  private fun MilitaryRecordPrisonDto.toResponseDto(): List<MilitaryRecordDto> {
    val rankSuffixList = setOf("\\(Army\\)", "\\(Navy\\)", "\\(RAF\\)", "\\(Royal Marines\\)")
    val rankSuffixPattern = Regex(rankSuffixList.joinToString("|"), RegexOption.IGNORE_CASE)
    return this.militaryRecords.map {
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
        militaryRankDescription = it.militaryRankDescription
          ?.replace(rankSuffixPattern, "")
          ?.trim(),
        serviceNumber = it.serviceNumber,
        disciplinaryActionCode = it.disciplinaryActionCode,
        disciplinaryActionDescription = it.disciplinaryActionDescription,
      )
    }
  }

  private fun PhysicalAttributesPrisonDto.toResponseDto() = PhysicalAttributesDto(
    height = height,
    weight = weight,
    hair = hair?.toReferenceDataValue(),
    facialHair = facialHair?.toReferenceDataValue(),
    face = face?.toReferenceDataValue(),
    build = build?.toReferenceDataValue(),
    leftEyeColour = leftEyeColour?.toReferenceDataValue(),
    rightEyeColour = rightEyeColour?.toReferenceDataValue(),
    shoeSize = shoeSize,
  )

  private fun CorePersonRecordReferenceDataValue.toReferenceDataValue() = ReferenceDataValue(
    id = "${domain}_$code",
    code,
    description,
  )

  private fun DistinguishingMarkPrisonDto.toResponseDto(): DistinguishingMarkDto = DistinguishingMarkDto(
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

