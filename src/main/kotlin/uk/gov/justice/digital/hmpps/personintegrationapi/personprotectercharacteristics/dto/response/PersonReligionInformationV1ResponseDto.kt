package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.response

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.common.ReligionDto

@Schema(description = "Persons religion information")
data class PersonReligionInformationV1ResponseDto(
  @Schema(description = "The persons current religion")
  val currentReligion: ReligionDto,

  @Schema(description = "Collection of historical religion information.")
  val religionHistory: Set<ReligionDto> = emptySet(),
)
