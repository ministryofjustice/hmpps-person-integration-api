package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception

class InvalidIdentifierException(type: String) : Exception("An invalid $type identifier value was provided")
