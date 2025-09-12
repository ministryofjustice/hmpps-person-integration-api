package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import java.time.LocalDate

data class MilitaryRecordPrisonDto(
  val militaryRecords: List<MilitaryRecord>,
)

data class MilitaryRecord(
  val bookingId: Long,
  val militarySeq: Int,
  val warZoneCode: String?,
  val warZoneDescription: String?,
  val startDate: LocalDate,
  val endDate: LocalDate?,
  val militaryDischargeCode: String?,
  val militaryDischargeDescription: String?,
  val militaryBranchCode: String,
  val militaryBranchDescription: String,
  val description: String?,
  val unitNumber: String?,
  val enlistmentLocation: String?,
  val dischargeLocation: String?,
  val selectiveServicesFlag: Boolean,
  val militaryRankCode: String?,
  val militaryRankDescription: String?,
  val serviceNumber: String?,
  val disciplinaryActionCode: String?,
  val disciplinaryActionDescription: String?,
) {
  fun toResponseDto(): MilitaryRecordDto {
    val rankSuffixPattern = Regex("(\\(Army\\)|\\(Navy\\)|\\(RAF\\)|\\(Royal Marines\\))", RegexOption.IGNORE_CASE)
    return MilitaryRecordDto(
      militarySeq = this.militarySeq,
      warZoneCode = this.warZoneCode,
      warZoneDescription = this.warZoneDescription,
      startDate = this.startDate,
      endDate = this.endDate,
      militaryDischargeCode = this.militaryDischargeCode,
      militaryDischargeDescription = this.militaryDischargeDescription,
      militaryBranchCode = this.militaryBranchCode,
      militaryBranchDescription = this.militaryBranchDescription,
      description = this.description,
      unitNumber = this.unitNumber,
      enlistmentLocation = this.enlistmentLocation,
      dischargeLocation = this.dischargeLocation,
      selectiveServicesFlag = this.selectiveServicesFlag,
      militaryRankCode = this.militaryRankCode,
      militaryRankDescription = this.militaryRankDescription?.replace(rankSuffixPattern, "")?.trim(),
      serviceNumber = this.serviceNumber,
      disciplinaryActionCode = this.disciplinaryActionCode,
      disciplinaryActionDescription = this.disciplinaryActionDescription,
    )
  }
}
