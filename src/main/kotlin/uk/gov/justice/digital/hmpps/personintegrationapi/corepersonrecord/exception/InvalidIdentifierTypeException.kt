package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception

class InvalidIdentifierTypeException(type: String) : Exception("Identifier type $type is invalid")
