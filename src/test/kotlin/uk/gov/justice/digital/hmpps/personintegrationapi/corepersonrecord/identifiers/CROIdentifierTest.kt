package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class CROIdentifierTest {
  @Test
  fun `should process an empty string`() {
    val identifier = CROIdentifier.from("")
    assertThat(identifier.croId.isEmpty())
  }

  @Test
  fun `should process null id`() {
    val identifier = CROIdentifier.from(null)
    assertThat(identifier.croId.isEmpty())
  }

  @Test
  fun `should process invalid id and not store it`() {
    val identifier = CROIdentifier.from("85227/65G")
    assertThat(identifier.croId.isEmpty())
  }

  @Test
  fun `should process a SF format CRO`() {
    val identifier = CROIdentifier.from("SF05/482703J")
    assertThat(identifier.croId).isEqualTo("SF05/482703J")
  }

  @Test
  fun `should process a SF format CRO with limit serial section should not pad serial num`() {
    val identifier = CROIdentifier.from("SF83/50058Z")
    assertThat(identifier.croId).isEqualTo("SF83/50058Z")
  }

  @Test
  fun `should process a standard format CRO`() {
    val identifier = CROIdentifier.from("265416/21G")
    assertThat(identifier.croId).isEqualTo("265416/21G")
  }

  @Test
  fun `should process a standard format CRO with limit serial section`() {
    val identifier = CROIdentifier.from("65656/91H")
    assertThat(identifier.croId).isEqualTo("065656/91H")
  }

  @Test
  fun `should process CROs`() {
    val readAllLines = Files.readAllLines(Paths.get("src/test/resources/identifiers/valid-cros.csv"), Charsets.UTF_8)

    readAllLines.stream().forEach {
      assertThat(CROIdentifier.from(it).croId).isNotBlank().withFailMessage(it)
    }
  }
}
