package uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import uk.gov.justice.digital.hmpps.personintegrationapi.common.AllowableValuesValidator
import kotlin.reflect.KClass

@Constraint(validatedBy = [AllowableValuesValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(
  AnnotationRetention.RUNTIME,
)
annotation class ValidAllowableValues(
  val message: String = "The value must be one of the allowed contact types.",
  val allowableValues: Array<String> = [],
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)
