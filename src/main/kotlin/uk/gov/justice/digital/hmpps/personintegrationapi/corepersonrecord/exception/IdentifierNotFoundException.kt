package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception

class IdentifierNotFoundException(offenderId: Long, seqId: Long) : Exception("An existing identifier for alias (offenderId) $offenderId with id $seqId was not found")
