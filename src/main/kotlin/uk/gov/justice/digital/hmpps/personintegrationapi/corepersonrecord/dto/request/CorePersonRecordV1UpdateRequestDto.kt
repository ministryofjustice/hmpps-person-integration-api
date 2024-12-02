package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.enumeration.CorePersonRecordField
import java.time.LocalDate

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "fieldName",
  visible = true,
)
@JsonSubTypes(
  JsonSubTypes.Type(name = "BIRTHPLACE", value = BirthplaceUpdateDto::class),
  JsonSubTypes.Type(name = "COUNTRY_OF_BIRTH", value = CountryOfBirthUpdateDto::class),
  JsonSubTypes.Type(name = "DATE_OF_BIRTH", value = DateOfBirthUpdateDto::class),
)
@Schema(description = "Core Person Record V1 update request base")
sealed class CorePersonRecordV1UpdateRequestDto {
  open val fieldName: Any? = null
  abstract val value: Any?
}

@Schema(description = "Core Person Record V1 birthplace update request")
data class BirthplaceUpdateDto(
  @Schema(
    description = "The new value for the Birthplace",
    example = "London",
    required = true,
    nullable = false,
  )
  @field:NotNull()
  override val value: String,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = ["BIRTHPLACE"],
    required = true,
    nullable = false,
  )
  override val fieldName: CorePersonRecordField = CorePersonRecordField.BIRTHPLACE
}

@Schema(description = "Core Person Record V1 date of birth update request")
data class DateOfBirthUpdateDto(
  @Schema(
    description = "The new value for the date of birth field",
    example = "01/01/2000",
    required = true,
    nullable = false,
  )
  @field:NotNull()
  @field:DateTimeFormat(pattern = "dd/mm/yyyy")
  override val value: LocalDate,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = ["DATE_OF_BIRTH"],
    required = true,
    nullable = false,
  )
  override val fieldName: CorePersonRecordField = CorePersonRecordField.DATE_OF_BIRTH
}

@Schema(description = "Core Person Record V1 country of birth update request")
data class CountryOfBirthUpdateDto(
  @Schema(
    description = "The new value for the country of brith field",
    example = "UK",
    required = true,
    nullable = false,
  )
  @field:NotNull()
  override val value: String,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = ["COUNTRY_OF_BIRTH"],
    required = true,
    nullable = false,
  )
  override val fieldName: CorePersonRecordField = CorePersonRecordField.COUNTRY_OF_BIRTH
}
