package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers

// Copied directly from https://github.com/ministryofjustice/hmpps-person-record/src/main/kotlin/uk/gov/justice/digital/hmpps/personrecord/model/identifiers/CROIdentifier.kt to duplicate the validation there
data class CROIdentifier(val croId: String) {

  override fun toString(): String = croId

  companion object {
    private const val EMPTY_CRO = ""
    private const val SLASH = "/"
    private const val SERIAL_NUM_LENGTH = 6

    private val SF_CRO_REGEX = Regex("^SF\\d{2}/\\d{1,$SERIAL_NUM_LENGTH}[A-Z]\$")
    private val CRO_REGEX = Regex("^\\d{1,$SERIAL_NUM_LENGTH}/\\d{2}[A-Z]\$")

    private fun invalidCro(): CROIdentifier = CROIdentifier(EMPTY_CRO)

    fun from(inputCroId: String? = EMPTY_CRO): CROIdentifier = when {
      inputCroId.isNullOrEmpty() -> invalidCro()
      isSfFormat(inputCroId) -> canonicalSfFormat(inputCroId)
      isStandardFormat(inputCroId) -> canonicalStandardFormat(inputCroId)
      else -> invalidCro()
    }

    private fun canonicalStandardFormat(inputCroId: String): CROIdentifier {
      val checkChar = inputCroId.takeLast(1).single()
      val (serialNum, yearDigits) = inputCroId.dropLast(1).split(SLASH) // splits into [NNNNNN, YY and drops D]
      return when {
        correctModulus(checkChar, padSerialNumber(serialNum), yearDigits) -> CROIdentifier(formatStandard(checkChar, serialNum, yearDigits))
        else -> invalidCro()
      }
    }

    private fun canonicalSfFormat(inputCroId: String): CROIdentifier {
      val checkChar = inputCroId.takeLast(1).single()
      val (yearDigits, serialNum) = inputCroId.drop(2).dropLast(1).split(SLASH) // splits into [YY, NNNNNN and drops D]
      return when {
        correctModulus(checkChar, serialNum, yearDigits) -> CROIdentifier(formatSF(checkChar, serialNum, yearDigits))
        else -> invalidCro()
      }
    }

    private fun correctModulus(checkChar: Char, serialNum: String, yearDigits: String): Boolean = checkChar == VALID_LETTERS[(yearDigits + serialNum).toInt().mod(VALID_LETTERS.length)]

    private fun formatStandard(checkChar: Char, serialNum: String, yearDigits: String) = "${padSerialNumber(serialNum)}/$yearDigits$checkChar"

    private fun formatSF(checkChar: Char, serialNum: String, yearDigits: String) = "SF$yearDigits/$serialNum$checkChar"

    const val VALID_LETTERS = "ZABCDEFGHJKLMNPQRTUVWXY"

    private fun isStandardFormat(inputCroId: String): Boolean = inputCroId.matches(CRO_REGEX)

    private fun isSfFormat(inputCroId: String): Boolean = inputCroId.matches(SF_CRO_REGEX)

    private fun padSerialNumber(serialNumber: String): String = serialNumber.padStart(SERIAL_NUM_LENGTH, '0')
  }
}
