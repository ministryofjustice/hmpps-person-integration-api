package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.common

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Religion information")
data class ReligionDto(

  @Schema(
    description = "The religion name",
    example = "No Religion",
    required = true,
    nullable = false,
  )
  val religion: String = "No Religion",

  @Schema(description = "The religion code", example = "NIL", required = true, nullable = false)
  val religionCode: String = "NIL",

  @Schema(
    description = "Comments on reason for adding religion",
    example = "Religious belief verified",
    required = false,
    nullable = true,
  )
  val changeReason: String? = null,

  @Schema(
    description = "The date the religious belief is valid from",
    example = "01/01/2024",
    required = true,
    nullable = false,
  )
  val effectiveFromDate: LocalDate = LocalDate.now(),

  @Schema(
    description = "The date the religious belief is valid until",
    example = "01/01/2024",
    required = false,
    nullable = true,
  )
  val effectiveToDate: LocalDate? = null,

  @Schema(description = "First name of staff member that added belief")
  val addedByFirstName: String = "TEST",

  @Schema(description = "Last name of staff member that added belief")
  val addedByLastName: String = "USER",

  @Schema(description = "First name of staff member that updated belief")
  val updatedByFirstName: String? = null,

  @Schema(description = "Last name of staff member that updated belief")
  val updatedByLastName: String? = null,

  @Schema(description = "Date belief was updated")
  val updatedDate: LocalDate? = null,

  @Schema(description = "Boolean flag indicating if the religious belief has been verified")
  val verified: Boolean = false,
)
