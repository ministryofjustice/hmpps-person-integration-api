package uk.gov.justice.digital.hmpps.personintegrationapi.common

object Constants {
  const val PRISONER_NUMBER_REGEX = "^[A-Za-z0-9]{1,10}\$"
  const val PRISONER_NUMBER_VALIDATION_MESSAGE = "The prisoner number must be a alphanumeric string upto 10 characters in length."
}
