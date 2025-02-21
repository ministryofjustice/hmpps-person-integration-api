package uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response

import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataValue

data class ReferenceDataCode(
  val domain: String,
  val code: String,
  val description: String,
  val activeFlag: String,
  val listSeq: Int,
  val parentCode: String? = null,
  val parentDomain: String? = null,
) {
  fun toReferenceDataValue(): ReferenceDataValue = ReferenceDataValue(
    id = "${domain}_$code",
    code = code,
    description = description,
  )
}
