package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper.mapRefDataDescription

@Schema(description = "Reference Data Value - a reference data code selected as the value for a field")
@JsonInclude(NON_NULL)
data class ReferenceDataValuePrisonDto(
  @Schema(description = "Domain", example = "domain")
  val domain: String,

  @Schema(description = "Code", example = "code")
  val code: String,

  @Schema(description = "Description of the reference data code", example = "Code description")
  val description: String,
) {
  fun toReferenceDataValue(): ReferenceDataValue = ReferenceDataValue(
    id = "${domain}_$code",
    code = code,
    description = mapRefDataDescription(domain, code, description),
  )
}
