package uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import uk.gov.justice.digital.hmpps.personintegrationapi.common.Constants

@Schema(
  description = Constants.PRISONER_NUMBER_VALIDATION_MESSAGE,
  example = "A12345",
  pattern = Constants.PRISONER_NUMBER_REGEX,
)
@Pattern(
  regexp = Constants.PRISONER_NUMBER_REGEX,
  message = Constants.PRISONER_NUMBER_VALIDATION_MESSAGE,
)
@Target(
  AnnotationTarget.FIELD,
  AnnotationTarget.VALUE_PARAMETER,
)
@Retention(
  AnnotationRetention.RUNTIME,
)
annotation class ValidPrisonerNumber
