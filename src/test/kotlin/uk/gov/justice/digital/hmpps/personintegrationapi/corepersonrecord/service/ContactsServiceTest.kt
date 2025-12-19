package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateEmailAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreatePhoneNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.EmailAddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.PhoneNumberPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.ContactRequestDto

@ExtendWith(MockitoExtension::class)
class ContactsServiceTest {
  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  lateinit var underTest: ContactsService

  @BeforeEach
  fun beforeEach() {
  }

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @Nested
  inner class GetContacts {
    private fun stubContactApis(
      phoneNumbers: ResponseEntity<List<PhoneNumberPrisonDto>>,
      emails: ResponseEntity<List<EmailAddressPrisonDto>>,
    ) {
      whenever(prisonApiClient.getPhoneNumbers(PERSON_ID)).thenReturn(
        phoneNumbers,
      )

      whenever(prisonApiClient.getEmailAddresses(PERSON_ID)).thenReturn(
        emails,
      )
    }

    private fun stubContactApis(
      phoneNumbers: List<PhoneNumberPrisonDto>,
      emails: List<EmailAddressPrisonDto>,
    ) {
      whenever(prisonApiClient.getPhoneNumbers(PERSON_ID)).thenReturn(
        ResponseEntity.ok(phoneNumbers),
      )

      whenever(prisonApiClient.getEmailAddresses(PERSON_ID)).thenReturn(
        ResponseEntity.ok(emails),
      )
    }

    @Test
    fun `returns a list of contacts from the prison API with a valid person ID`() {
      stubContactApis(listOf(PRISON_PHONE_NUMBER_ONE), listOf(PRISON_EMAIL_ADDRESS_ONE))

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!.toList()
      assertThat(responseBody.size).isEqualTo(2)
      assertThat(responseBody[0].contactId).isEqualTo(PRISON_PHONE_NUMBER_ONE.phoneId)
      assertThat(responseBody[0].contactType).isEqualTo(PRISON_PHONE_NUMBER_ONE.type)
      assertThat(responseBody[0].contactValue).isEqualTo(PRISON_PHONE_NUMBER_ONE.number)
      assertThat(responseBody[1].contactId).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.emailAddressId)
      assertThat(responseBody[1].contactType).isEqualTo("EMAIL")
      assertThat(responseBody[1].contactValue).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.email)
    }

    @Test
    fun `returns an empty list when a person is found with no contacts`() {
      stubContactApis(listOf(), listOf())

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!
      assertThat(responseBody.size).isEqualTo(0)
    }

    @Test
    fun `returns correctly when phones are empty`() {
      stubContactApis(listOf(), listOf(PRISON_EMAIL_ADDRESS_ONE))

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!.toList()
      assertThat(responseBody.size).isEqualTo(1)
      assertThat(responseBody[0].contactId).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.emailAddressId)
      assertThat(responseBody[0].contactType).isEqualTo("EMAIL")
      assertThat(responseBody[0].contactValue).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.email)
    }

    @Test
    fun `returns correctly when emails are empty`() {
      stubContactApis(listOf(PRISON_PHONE_NUMBER_ONE), listOf())

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!.toList()
      assertThat(responseBody.size).isEqualTo(1)
      assertThat(responseBody[0].contactId).isEqualTo(PRISON_PHONE_NUMBER_ONE.phoneId)
      assertThat(responseBody[0].contactType).isEqualTo(PRISON_PHONE_NUMBER_ONE.type)
      assertThat(responseBody[0].contactValue).isEqualTo(PRISON_PHONE_NUMBER_ONE.number)
    }

    @Test
    fun `returns the status from the API when it returns unsuccessful - Phones`() {
      stubContactApis(
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).build(),
        ResponseEntity.ok(listOf(PRISON_EMAIL_ADDRESS_ONE)),
      )

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_GATEWAY)
    }

    @Test
    fun `returns the status from the API when it returns unsuccessful - Emails`() {
      stubContactApis(
        ResponseEntity.ok(listOf(PRISON_PHONE_NUMBER_ONE)),
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).build(),
      )

      val response = underTest.getContacts(PERSON_ID)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_GATEWAY)
    }
  }

  @Nested
  inner class CreateContact {
    private fun stubPhone(phone: ResponseEntity<PhoneNumberPrisonDto>) = whenever(
      prisonApiClient.createPhoneNumber(
        eq(PERSON_ID),
        any(),
      ),
    ).thenReturn(phone)

    private fun stubEmail(email: ResponseEntity<EmailAddressPrisonDto>) = whenever(
      prisonApiClient.createEmailAddress(
        eq(PERSON_ID),
        any(),
      ),
    ).thenReturn(email)

    @Nested
    inner class Validations {
      @ParameterizedTest
      @MethodSource("uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.ContactsServiceTest#validationTestCases")
      fun `Phone and email validations`(type: String, value: String, extensionValue: String?, valid: Boolean) {
        stubEmail(ResponseEntity.ok(PRISON_EMAIL_ADDRESS_ONE))
        stubPhone(ResponseEntity.ok(PRISON_PHONE_NUMBER_ONE))

        val response = underTest.createContact(
          PERSON_ID,
          ContactRequestDto(contactType = type, contactValue = value, contactPhoneExtension = extensionValue),
        )
        if (valid) {
          assertThat(response.statusCode.is2xxSuccessful).isTrue()
        } else {
          assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }
      }
    }

    @Test
    fun `Phone - creates and returns the new phone number when given a phone number type`() {
      stubPhone(ResponseEntity.ok(PRISON_PHONE_NUMBER_ONE))

      val response =
        underTest.createContact(
          PERSON_ID,
          ContactRequestDto(contactType = "MOB", contactValue = "01234321", contactPhoneExtension = "1234"),
        )

      verify(
        prisonApiClient,
      ).createPhoneNumber(
        PERSON_ID,
        CreatePhoneNumber("MOB", "01234321", "1234"),
      )

      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!
      assertThat(responseBody.contactId).isEqualTo(PRISON_PHONE_NUMBER_ONE.phoneId)
      assertThat(responseBody.contactType).isEqualTo(PRISON_PHONE_NUMBER_ONE.type)
      assertThat(responseBody.contactValue).isEqualTo(PRISON_PHONE_NUMBER_ONE.number)
      assertThat(responseBody.contactPhoneExtension).isEqualTo(PRISON_PHONE_NUMBER_ONE.ext)
    }

    @Test
    fun `Phone - returns the status from the API when it returns unsuccessful`() {
      stubPhone(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())

      val response =
        underTest.createContact(PERSON_ID, ContactRequestDto(contactType = "MOB", contactValue = "01234321"))

      assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Email - sanitises input and creates and returns an email when given an email type`() {
      stubEmail(ResponseEntity.ok(PRISON_EMAIL_ADDRESS_ONE))

      val response =
        underTest.createContact(PERSON_ID, ContactRequestDto(contactType = "EMAIL", contactValue = "  prisoner@home.com  "))

      verify(
        prisonApiClient,
      ).createEmailAddress(
        PERSON_ID,
        CreateEmailAddress("prisoner@home.com"),
      )

      assertThat(response.statusCode.is2xxSuccessful).isTrue()
      val responseBody = response.body!!
      assertThat(responseBody.contactId).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.emailAddressId)
      assertThat(responseBody.contactType).isEqualTo("EMAIL")
      assertThat(responseBody.contactValue).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.email)
    }

    @Test
    fun `Email - returns the status from the API when it returns unsuccessful`() {
      stubEmail(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())

      val response =
        underTest.createContact(PERSON_ID, ContactRequestDto(contactType = "EMAIL", contactValue = "prisoner@home.com"))

      assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  @Nested
  inner class UpdateContact {
    private fun stubPhone(phone: ResponseEntity<PhoneNumberPrisonDto>) = whenever(
      prisonApiClient.updatePhoneNumber(
        eq(PERSON_ID),
        eq(PHONE_NUMBER_ID),
        any(),
      ),
    ).thenReturn(phone)

    private fun stubEmail(email: ResponseEntity<EmailAddressPrisonDto>) = whenever(
      prisonApiClient.updateEmailAddress(
        eq(PERSON_ID),
        eq(EMAIL_ADDRESS_ID),
        any(),
      ),
    ).thenReturn(email)

    @Nested
    inner class Validations {
      @ParameterizedTest
      @MethodSource("uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.ContactsServiceTest#validationTestCases")
      fun `Phone and email validations`(type: String, value: String, extensionValue: String?, valid: Boolean) {
        val contactId = if (type == "EMAIL") {
          stubEmail(ResponseEntity.ok(PRISON_EMAIL_ADDRESS_ONE))
          EMAIL_ADDRESS_ID
        } else {
          stubPhone(ResponseEntity.ok(PRISON_PHONE_NUMBER_ONE))
          PHONE_NUMBER_ID
        }

        val response = underTest.updateContact(
          PERSON_ID,
          contactId,
          ContactRequestDto(contactType = type, contactValue = value, contactPhoneExtension = extensionValue),
        )
        if (valid) {
          assertThat(response.statusCode.is2xxSuccessful).isTrue()
        } else {
          assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }
      }
    }

    @Test
    fun `Updates and returns the new phone number when given a phone number type`() {
      stubPhone(ResponseEntity.ok(PRISON_PHONE_NUMBER_ONE))

      val response = underTest.updateContact(
        PERSON_ID,
        PHONE_NUMBER_ID,
        ContactRequestDto(contactType = "MOB", contactValue = "01234321", contactPhoneExtension = "1234"),
      )

      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      verify(
        prisonApiClient,
      ).updatePhoneNumber(
        PERSON_ID,
        PHONE_NUMBER_ID,
        CreatePhoneNumber("MOB", "01234321", "1234"),
      )

      val responseBody = response.body!!
      assertThat(responseBody.contactId).isEqualTo(PRISON_PHONE_NUMBER_ONE.phoneId)
      assertThat(responseBody.contactType).isEqualTo(PRISON_PHONE_NUMBER_ONE.type)
      assertThat(responseBody.contactValue).isEqualTo(PRISON_PHONE_NUMBER_ONE.number)
    }

    @Test
    fun `Phone - returns the status from the API when it returns unsuccessful`() {
      stubPhone(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())

      val response = underTest.updateContact(
        PERSON_ID,
        PHONE_NUMBER_ID,
        ContactRequestDto(contactType = "MOB", contactValue = "01234321"),
      )

      assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Sanitises input and updates and returns the new email address when given an email type`() {
      stubEmail(ResponseEntity.ok(PRISON_EMAIL_ADDRESS_ONE))

      val response = underTest.updateContact(
        PERSON_ID,
        EMAIL_ADDRESS_ID,
        ContactRequestDto(contactType = "EMAIL", contactValue = "  foo@bar.com  "),
      )

      verify(
        prisonApiClient,
      ).updateEmailAddress(
        PERSON_ID,
        EMAIL_ADDRESS_ID,
        CreateEmailAddress("foo@bar.com"),
      )

      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!
      assertThat(responseBody.contactId).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.emailAddressId)
      assertThat(responseBody.contactType).isEqualTo("EMAIL")
      assertThat(responseBody.contactValue).isEqualTo(PRISON_EMAIL_ADDRESS_ONE.email)
    }

    @Test
    fun `Email - returns the status from the API when it returns unsuccessful`() {
      stubEmail(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())

      val response = underTest.updateContact(
        PERSON_ID,
        EMAIL_ADDRESS_ID,
        ContactRequestDto(contactType = "EMAIL", contactValue = "foo@bar.com"),
      )

      assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  private companion object {
    const val PERSON_ID = "ABC123"
    const val PHONE_NUMBER_ID = 567L
    const val EMAIL_ADDRESS_ID = 765L

    val PRISON_PHONE_NUMBER_ONE =
      PhoneNumberPrisonDto(phoneId = 123L, number = "01234 567 890", type = "BUS", ext = "123")
    val PRISON_EMAIL_ADDRESS_ONE = EmailAddressPrisonDto(emailAddressId = 321L, email = "prisoner@home.com")

    @JvmStatic
    fun validationTestCases(): List<Arguments> = listOf(
      // Emails
      Arguments.of("EMAIL", "@" + "1".repeat(240), null, false),
      Arguments.of("EMAIL", "1".repeat(240), null, false),
      Arguments.of("EMAIL", "@" + "1".repeat(239), null, true),
      Arguments.of("EMAIL", "@" + "1".repeat(239), null, true),
      Arguments.of("EMAIL", "   test@domain.com   ", null, true),
      Arguments.of("EMAIL", "test@ domain.com", null, false),

      // Phones without extensions
      Arguments.of("MOB", "1".repeat(41), null, false),
      Arguments.of("MOB", "1".repeat(40), null, true),

      // Phones with extensions
      Arguments.of("MOB", "1".repeat(40), "1".repeat(8), false),
      Arguments.of("MOB", "1".repeat(40), "1".repeat(7), true),
    )
  }
}
