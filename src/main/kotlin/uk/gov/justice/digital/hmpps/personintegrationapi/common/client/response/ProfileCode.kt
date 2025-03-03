package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue
import uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper.mapRefDataDescription
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Profile Code Type")
data class ProfileCodeType(
  @Schema(description = "Profile Type", required = true)
  val type: String,

  @Schema(description = "Category", required = true)
  val category: String,

  @Schema(description = "Description")
  val description: String? = null,

  @Schema(description = "Mandatory Flag", required = true)
  val mandatory: Boolean = false,

  @Schema(description = "Update Allowed Flag", required = true)
  val updateAllowed: Boolean = true,

  @Schema(description = "Code Value Type", required = true)
  val codeValueType: String,

  @Schema(description = "Active Flag", required = true)
  val active: Boolean = true,

  @Schema(description = "Expiry Date")
  val endDate: LocalDate? = null,

  @Schema(description = "List Sequence", required = true)
  val listSequence: Int = 99,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Profile Code ID")
data class ProfileCodeId(
  @Schema(description = "Type", required = true)
  val type: ProfileCodeType,

  @Schema(description = "Code", required = true)
  val code: String,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Profile Code")
data class ProfileCode(
  @Schema(description = "ID", required = true)
  val id: ProfileCodeId,

  @Schema(description = "Description")
  val description: String? = null,

  @Schema(description = "Update Allowed Flag", required = true)
  val updateAllowed: Boolean = true,

  @Schema(description = "Active Flag", required = true)
  val active: Boolean = true,

  @Schema(description = "Expiry Date")
  val endDate: LocalDate? = null,

  @Schema(description = "List Sequence", required = true)
  val listSequence: Int = 99,
) {
  fun toReferenceDataValue(): ReferenceDataValue = ReferenceDataValue(
    id = "${id.type.type}_${id.code}",
    code = id.code,
    description = mapRefDataDescription(id.type.type, id.code, description),
  )
}
