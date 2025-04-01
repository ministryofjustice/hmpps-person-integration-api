package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "fieldName",
  visible = true,
)
@JsonSubTypes(
  JsonSubTypes.Type(name = CorePersonRecordV1UpdateRequestDto.BIRTHPLACE, value = BirthplaceUpdateDto::class),
  JsonSubTypes.Type(name = CorePersonRecordV1UpdateRequestDto.COUNTRY_OF_BIRTH, value = CountryOfBirthUpdateDto::class),
  JsonSubTypes.Type(name = CorePersonRecordV1UpdateRequestDto.DATE_OF_BIRTH, value = DateOfBirthUpdateDto::class),
  JsonSubTypes.Type(name = CorePersonRecordV1UpdateRequestDto.SEXUAL_ORIENTATION, value = SexualOrientationUpdateDto::class),
)
@Schema(description = "Core Person Record V1 update request base")
sealed class CorePersonRecordV1UpdateRequestDto {
  abstract val fieldName: String
  abstract val value: Any?

  companion object {
    const val BIRTHPLACE = "BIRTHPLACE"
    const val COUNTRY_OF_BIRTH = "COUNTRY_OF_BIRTH"
    const val DATE_OF_BIRTH = "DATE_OF_BIRTH"
    const val SEXUAL_ORIENTATION = "SEXUAL_ORIENTATION"
  }
}

@Schema(description = "Core Person Record V1 birthplace update request")
data class BirthplaceUpdateDto(
  @Schema(
    description = "The new value for the birthplace",
    example = "London",
    required = true,
    nullable = true,
  )
  override val value: String?,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = [BIRTHPLACE],
    required = true,
    nullable = false,
  )
  override val fieldName: String = BIRTHPLACE
}

@Schema(description = "Core Person Record V1 date of birth update request")
data class DateOfBirthUpdateDto(
  @Schema(
    description = "The new value for the date of birth field",
    example = "01/01/2000",
    required = true,
    nullable = true,
  )
  @field:DateTimeFormat(pattern = "dd/mm/yyyy")
  override val value: LocalDate?,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = [DATE_OF_BIRTH],
    required = true,
    nullable = false,
  )
  override val fieldName: String = DATE_OF_BIRTH
}

@Schema(description = "Core Person Record V1 country of birth update request")
data class CountryOfBirthUpdateDto(
  @Schema(
    description = "The new value for the country of birth field",
    example = "UK",
    required = true,
    nullable = true,
  )
  override val value: String?,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = [COUNTRY_OF_BIRTH],
    required = true,
    nullable = false,
  )
  override val fieldName: String = COUNTRY_OF_BIRTH
}

@Schema(description = "Core Person Record V1 sexual orientation update request")
data class SexualOrientationUpdateDto(
  @Schema(
    description = "The new value for the sexual orientation field",
    example = "HET",
    required = true,
    nullable = true,
  )
  override val value: String?,
) : CorePersonRecordV1UpdateRequestDto() {
  @Schema(
    type = "String",
    description = "The field to be updated",
    allowableValues = [SEXUAL_ORIENTATION],
    required = true,
    nullable = false,
  )
  override val fieldName: String = SEXUAL_ORIENTATION
}
