package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception

class IdentifierNotFoundException(prisonerNumber: String, seqId: Long) : Exception("An existing identifier for prisoner $prisonerNumber with id $seqId was not found")
