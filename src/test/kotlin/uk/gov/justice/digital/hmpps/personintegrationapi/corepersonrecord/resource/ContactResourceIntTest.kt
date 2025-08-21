package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PHONE_NUMBER_ID
import uk.gov.justice.digital.hmpps.personintegrationapi.integration.wiremock.PRISONER_NUMBER

class ContactResourceIntTest : IntegrationTestBase() {
  @DisplayName("GET v1/person/{personId}/contacts")
  @Nested
  inner class ReadContactsByPersonIdTest {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.get().uri(READ_URL)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.get().uri(READ_URL)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can get contacts`() {
        webTestClient.get().uri(READ_URL)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .exchange().expectStatus().isOk.expectBody().jsonPath("$.size()").isEqualTo(4)
          .jsonPath("$[0].contactId").isEqualTo("101").jsonPath("$[0].contactValue").isEqualTo("09876 543 210").jsonPath("$[0].contactType").isEqualTo("HOME")
          .jsonPath("$[1].contactId").isEqualTo("102").jsonPath("$[1].contactValue").isEqualTo("01234 567890").jsonPath("$[1].contactType").isEqualTo("BUS").jsonPath("$[1].contactPhoneExtension").isEqualTo("111")
          .jsonPath("$[2].contactId").isEqualTo("201").jsonPath("$[2].contactValue").isEqualTo("foo@bar.com").jsonPath("$[2].contactType").isEqualTo("EMAIL")
          .jsonPath("$[3].contactId").isEqualTo("202").jsonPath("$[3].contactValue").isEqualTo("bar@foo.com").jsonPath("$[3].contactType").isEqualTo("EMAIL")
      }
    }
  }

  @DisplayName("POST v1/person/{personId}/contacts")
  @Nested
  inner class CreateContactsByPersonIdTest {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.post().uri(CREATE_URL).contentType(MediaType.APPLICATION_JSON).bodyValue(VALID_POST_REQUEST_BODY)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.post().uri(CREATE_URL).contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).bodyValue(VALID_POST_REQUEST_BODY).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class Validation {
      private fun post(body: String) = expectBadRequest(webTestClient.post(), CREATE_URL, body)

      @Test
      fun `invalid type`() {
        post(INVALID_POST_REQUEST_BODY__INVALID_TYPE)
      }

      @Test
      fun `Email - empty`() {
        post(INVALID_POST_REQUEST_BODY__EMAIL__EMPTY)
      }

      @Test
      fun `Email - too long`() {
        post(INVALID_POST_REQUEST_BODY__EMAIL__TOO_LONG)
      }

      @Test
      fun `Phone - empty`() {
        post(INVALID_POST_REQUEST_BODY__PHONE_NUMBER__EMPTY)
      }

      @Test
      fun `Phone - too long`() {
        post(INVALID_POST_REQUEST_BODY__PHONE_NUMBER__TOO_LONG)
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can post new contact`() {
        webTestClient.post().uri(CREATE_URL).contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_POST_REQUEST_BODY).exchange().expectStatus().isOk.expectBody().jsonPath("$.contactId")
          .isEqualTo(203)
      }
    }
  }

  @DisplayName("PUT v1/person/{personId}/contacts/{contactId}")
  @Nested
  inner class UpdateContactsByPersonIdAndContactIdTest {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        webTestClient.put().uri(UPDATE_URL).contentType(MediaType.APPLICATION_JSON).bodyValue(VALID_PUT_REQUEST_BODY)
          .exchange().expectStatus().isUnauthorized
      }

      @Test
      fun `access forbidden with wrong role`() {
        webTestClient.put().uri(UPDATE_URL).contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf("ROLE_IS_WRONG"))).bodyValue(VALID_PUT_REQUEST_BODY).exchange()
          .expectStatus().isForbidden
      }
    }

    @Nested
    inner class Validation {
      private fun put(body: String) = expectBadRequest(webTestClient.put(), UPDATE_URL, body)

      @Test
      fun `invalid type`() {
        put(INVALID_POST_REQUEST_BODY__INVALID_TYPE)
      }

      @Test
      fun `Email - empty`() {
        put(INVALID_POST_REQUEST_BODY__EMAIL__EMPTY)
      }

      @Test
      fun `Email - too long`() {
        put(INVALID_POST_REQUEST_BODY__EMAIL__TOO_LONG)
      }

      @Test
      fun `Phone - empty`() {
        put(INVALID_POST_REQUEST_BODY__PHONE_NUMBER__EMPTY)
      }

      @Test
      fun `Phone - too long`() {
        put(INVALID_POST_REQUEST_BODY__PHONE_NUMBER__TOO_LONG)
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `can update contact`() {
        webTestClient.put().uri(UPDATE_URL).contentType(MediaType.APPLICATION_JSON)
          .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
          .bodyValue(VALID_PUT_REQUEST_BODY).exchange().expectStatus().isOk.expectBody().jsonPath("$.contactId")
          .isEqualTo(103)
      }
    }
  }

  private fun expectBadRequest(webRequestSpec: WebTestClient.RequestBodyUriSpec, uri: String, body: String) = webRequestSpec.uri(uri).contentType(MediaType.APPLICATION_JSON)
    .headers(setAuthorisation(roles = listOf(CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE)))
    .bodyValue(body).exchange().expectStatus().isBadRequest

  private companion object {
    const val CREATE_URL = "/v1/person/$PRISONER_NUMBER/contacts"
    const val READ_URL = "/v1/person/$PRISONER_NUMBER/contacts"
    const val UPDATE_URL = "/v1/person/$PRISONER_NUMBER/contacts/$PHONE_NUMBER_ID"

    const val INVALID_POST_REQUEST_BODY__INVALID_TYPE =
      //language=json
      """
        { "contactType": "CATS", "contactValue": "foo@bar.com" }
      """

    const val INVALID_POST_REQUEST_BODY__EMAIL__EMPTY =
      //language=json
      """
        { "contactType": "EMAIL", "contactValue": "" }
      """

    val emailTooLong = "1".repeat(241)
    val INVALID_POST_REQUEST_BODY__EMAIL__TOO_LONG =
      " { \"contactType\": \"EMAIL\", \"contactValue\": \"$emailTooLong\" }"

    const val INVALID_POST_REQUEST_BODY__PHONE_NUMBER__EMPTY =
      //language=json
      """
        { "contactType": "MOB", "contactValue": "" }
      """

    val phoneTooLong = "1".repeat(41)
    val INVALID_POST_REQUEST_BODY__PHONE_NUMBER__TOO_LONG =
      " { \"contactType\": \"MOB\", \"contactValue\": \"$phoneTooLong\" }"

    const val VALID_POST_REQUEST_BODY =
      //language=json
      """
        { "contactType": "EMAIL", "contactValue": "prisoner@home.com" }
      """

    const val VALID_PUT_REQUEST_BODY =
      //language=json
      """
        { "contactType": "BUS", "contactValue": "12312 123 123", "contactExtension": "123" }
      """
  }
}
