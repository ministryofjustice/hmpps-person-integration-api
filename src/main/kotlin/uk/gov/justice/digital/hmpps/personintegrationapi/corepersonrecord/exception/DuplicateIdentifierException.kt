package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception

class DuplicateIdentifierException(prisonerNumber: String, type: String) : Exception("Prisoner $prisonerNumber already has a $type identifier with the same value")
