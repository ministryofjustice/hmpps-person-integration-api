package uk.gov.justice.digital.hmpps.personintegrationapi.common

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidAllowableValues

class AllowableValuesValidator : ConstraintValidator<ValidAllowableValues, String> {
  private var allowableValues: List<String>? = null

  override fun initialize(constraintAnnotation: ValidAllowableValues) {
    this.allowableValues = constraintAnnotation.allowableValues.toList()
  }

  override fun isValid(
    value: String?,
    context: ConstraintValidatorContext?,
  ): Boolean = if (value == null) false else allowableValues!!.contains(value)
}
