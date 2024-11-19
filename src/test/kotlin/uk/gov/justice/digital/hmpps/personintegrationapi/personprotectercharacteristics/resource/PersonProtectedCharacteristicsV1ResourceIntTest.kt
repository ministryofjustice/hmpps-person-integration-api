package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase

class PersonProtectedCharacteristicsV1ResourceIntTest : IntegrationTestBase() {

  @DisplayName("PUT v1/person-protected-characteristics/religion")
  @Nested
  inner class PutReligionByPrisonerNumberTest

  @DisplayName("GET v1/person-protected-characteristics/reference-data/domain/{domain}/codes")
  @Nested
  inner class GetReferenceDataCodesByDomain
}
