package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue

@Schema(description = "Address type holding information about the address usage.")
@JsonInclude(NON_NULL)
data class AddressTypeDto(
  @Schema(description = "The address usage type. The must be one of the ADDRESS_TYPE reference daa domain codes")
  val addressUsageType: ReferenceDataValue,

  @Schema(description = "Boolean indicating whether this is an active address usage.", example = "true")
  val active: Boolean? = null,
)
