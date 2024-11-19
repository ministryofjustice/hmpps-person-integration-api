package uk.gov.justice.digital.hmpps.personintegrationapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HmppsPersonIntegrationApi

fun main(args: Array<String>) {
  runApplication<HmppsPersonIntegrationApi>(*args)
}
