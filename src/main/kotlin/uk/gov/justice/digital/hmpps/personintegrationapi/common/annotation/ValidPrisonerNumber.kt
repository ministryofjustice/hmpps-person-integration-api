package uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.Pattern
import uk.gov.justice.digital.hmpps.personintegrationapi.common.Constants

@Parameter(
  description = Constants.PRISONER_NUMBER_VALIDATION_MESSAGE,
  example = "A12345",
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
