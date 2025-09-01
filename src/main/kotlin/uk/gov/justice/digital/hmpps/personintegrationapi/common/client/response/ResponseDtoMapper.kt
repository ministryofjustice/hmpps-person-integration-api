package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.SourceSystem
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.AddressTypeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ContactResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkImageDetail
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto

object ResponseDtoMapper {

  fun toAddressResponseDto(dto: AddressPrisonDto, personId: String): AddressResponseDto =
    AddressResponseDto(
      addressId = dto.addressId,
      personId = personId,
      uprn = null,
      noFixedAbode = dto.noFixedAddress,
      subBuildingName = dto.flat,
      buildingNumber = null,
      buildingName = dto.premise,
      thoroughfareName = dto.street,
      dependantLocality = dto.locality,
      postTown = dto.townCode?.let { ReferenceDataValue("CITY_$it", it, dto.town!!) },
      county = dto.countyCode?.let { ReferenceDataValue("COUNTY_$it", it, dto.county!!) },
      country = dto.countryCode?.let { ReferenceDataValue("COUNTRY_$it", it, dto.country!!) },
      postCode = dto.postalCode,
      fromDate = dto.startDate,
      toDate = dto.endDate,
      addressTypes = dto.addressUsages.map {
        AddressTypeDto(
          active = it.activeFlag,
          addressUsageType = ReferenceDataValue("ADDRESS_TYPE_${it.addressUsage}", it.addressUsage, it.addressUsageDescription),
        )
      },
      primaryAddress = dto.primary,
      postalAddress = dto.mail,
      comment = dto.comment,
      addressPhoneNumbers = dto.phones.map {
        ContactResponseDto(it.phoneId, it.type, it.number, it.ext)
      },
    )

  fun toPseudonymResponseDto(dto: CorePersonRecordAlias): PseudonymResponseDto =
    PseudonymResponseDto(
      sourceSystemId = dto.offenderId,
      sourceSystem = SourceSystem.NOMIS,
      prisonerNumber = dto.prisonerNumber,
      isWorkingName = dto.isWorkingName,
      firstName = dto.firstName,
      middleName1 = dto.middleName1,
      middleName2 = dto.middleName2,
      lastName = dto.lastName,
      dateOfBirth = dto.dateOfBirth,
      nameType = dto.nameType?.toReferenceDataValue(),
      title = dto.title?.toReferenceDataValue(),
      sex = dto.sex?.toReferenceDataValue(),
      ethnicity = dto.ethnicity?.toReferenceDataValue(),
    )

  fun toContactsResponseDto(phones: List<PhoneNumberPrisonDto>, emails: List<EmailAddressPrisonDto>): List<ContactResponseDto> =
    buildList {
      phones.forEach { add(ContactResponseDto(it.phoneId, it.type, it.number, it.ext)) }
      emails.forEach { add(ContactResponseDto(it.emailAddressId, "EMAIL", it.email)) }
    }

  fun toMilitaryRecordDtos(dto: MilitaryRecordPrisonDto): List<MilitaryRecordDto> {
    val rankSuffixPattern = Regex("(\\(Army\\)|\\(Navy\\)|\\(RAF\\)|\\(Royal Marines\\))", RegexOption.IGNORE_CASE)
    return dto.militaryRecords.map {
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
  }

  fun toPhysicalAttributesDto(dto: PhysicalAttributesPrisonDto): PhysicalAttributesDto =
    PhysicalAttributesDto(
      height = dto.height,
      weight = dto.weight,
      hair = dto.hair?.toReferenceDataValue(),
      facialHair = dto.facialHair?.toReferenceDataValue(),
      face = dto.face?.toReferenceDataValue(),
      build = dto.build?.toReferenceDataValue(),
      leftEyeColour = dto.leftEyeColour?.toReferenceDataValue(),
      rightEyeColour = dto.rightEyeColour?.toReferenceDataValue(),
      shoeSize = dto.shoeSize,
    )

  fun toDistinguishingMarkDto(dto: DistinguishingMarkPrisonDto): DistinguishingMarkDto =
    DistinguishingMarkDto(
      id = dto.id,
      bookingId = dto.bookingId,
      offenderNo = dto.offenderNo,
      bodyPart = dto.bodyPart?.toReferenceDataValue(),
      markType = dto.markType?.toReferenceDataValue(),
      side = dto.side?.toReferenceDataValue(),
      partOrientation = dto.partOrientation?.toReferenceDataValue(),
      comment = dto.comment,
      createdAt = dto.createdAt,
      createdBy = dto.createdBy,
      photographUuids = dto.photographUuids.map { DistinguishingMarkImageDetail(it.id, it.latest) },
    )

  private fun CorePersonRecordReferenceDataValue.toReferenceDataValue(): ReferenceDataValue =
    ReferenceDataValue("${domain}_$code", code, description)
}
