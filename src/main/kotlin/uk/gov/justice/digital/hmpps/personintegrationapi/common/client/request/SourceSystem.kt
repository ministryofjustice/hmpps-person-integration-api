package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request

enum class SourceSystem {
  NOMIS,
}

fun String.toSourceSystem() = SourceSystem.valueOf(this.trim().uppercase())
