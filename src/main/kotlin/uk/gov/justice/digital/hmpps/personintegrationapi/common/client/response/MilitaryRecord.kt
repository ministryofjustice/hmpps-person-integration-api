package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

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
)
