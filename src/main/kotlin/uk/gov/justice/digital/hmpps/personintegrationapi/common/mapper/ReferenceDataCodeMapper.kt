package uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper

/*
 * Map some NOMIS descriptions to new DPS descriptions based on `domain` and `code`
 */
object ReferenceDataCodeMapper {
  val referenceDataCodeDescriptionMappings = mapOf(
    "HAIR" to mapOf(
      "MOUSE" to "Mousy",
    ),
    "FACIAL_HAIR" to mapOf(
      "CLEAN SHAVEN" to "No facial hair",
      "MOUSTACHE" to "Moustache",
    ),
    "FACE" to mapOf(
      "BULLET" to "Long",
      "TRIANGULAR" to "Triangle",
    ),
    "BUILD" to mapOf(),
  )
}

fun mapRefDataDescription(domain: String, code: String, description: String?): String = ReferenceDataCodeMapper.referenceDataCodeDescriptionMappings[domain]?.get(code) ?: description ?: ""
