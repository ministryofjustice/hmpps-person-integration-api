package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateAddress
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.AddressUsage
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.Telephone
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.AddressRequestDto
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AddressServiceTest {
  @Mock
  lateinit var prisonApiClient: PrisonApiClient

  @InjectMocks
  lateinit var underTest: AddressService

  @BeforeEach
  fun beforeEach() {
  }

  @AfterEach
  fun afterEach() {
    reset(prisonApiClient)
  }

  @Nested
  inner class GetAddresses {
    private fun stubAddressApi(addresses: List<AddressPrisonDto>) {
      whenever(prisonApiClient.getAddresses(PERSON_ID)).thenReturn(ResponseEntity.ok(addresses))
    }

    @Test
    fun `returns a list of addresses from the prison API`() {
      stubAddressApi(listOf(PRISON_ADDRESS_1))

      val response = underTest.getAddresses(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!.toList()
      assertThat(responseBody).hasSize(1)
      assertThat(responseBody[0].addressId).isEqualTo(PRISON_ADDRESS_1.addressId)
      assertThat(responseBody[0].personId).isEqualTo(PERSON_ID)
      assertThat(responseBody[0].uprn).isNull()
      assertThat(responseBody[0].noFixedAbode).isTrue()
      assertThat(responseBody[0].buildingNumber).isEqualTo(PRISON_ADDRESS_1.flat)
      assertThat(responseBody[0].subBuildingName).isNull()
      assertThat(responseBody[0].buildingName).isEqualTo(PRISON_ADDRESS_1.premise)
      assertThat(responseBody[0].thoroughfareName).isEqualTo(PRISON_ADDRESS_1.street)
      assertThat(responseBody[0].dependantLocality).isEqualTo(PRISON_ADDRESS_1.locality)
      assertThat(responseBody[0].postTown?.description).isEqualTo(PRISON_ADDRESS_1.town)
      assertThat(responseBody[0].postTown?.code).isEqualTo(PRISON_ADDRESS_1.townCode)
      assertThat(responseBody[0].county?.description).isEqualTo(PRISON_ADDRESS_1.county)
      assertThat(responseBody[0].county?.code).isEqualTo(PRISON_ADDRESS_1.countyCode)
      assertThat(responseBody[0].country?.description).isEqualTo(PRISON_ADDRESS_1.country)
      assertThat(responseBody[0].country?.code).isEqualTo(PRISON_ADDRESS_1.countryCode)
      assertThat(responseBody[0].postCode).isEqualTo(PRISON_ADDRESS_1.postalCode)
      assertThat(responseBody[0].fromDate).isEqualTo(PRISON_ADDRESS_1.startDate)
      assertThat(responseBody[0].toDate).isEqualTo(PRISON_ADDRESS_1.endDate)
      assertThat(responseBody[0].addressTypes).hasSize(1)
      assertThat(responseBody[0].addressTypes.first().active).isTrue()
      assertThat(responseBody[0].addressTypes.first().addressUsageType.code).isEqualTo("HOME")
      assertThat(responseBody[0].addressTypes.first().addressUsageType.description).isEqualTo("Home")
      assertThat(responseBody[0].primaryAddress).isTrue()
      assertThat(responseBody[0].postalAddress).isTrue()
      assertThat(responseBody[0].comment).isEqualTo("Some comment")
      assertThat(responseBody[0].addressPhoneNumbers).hasSize(1)
      assertThat(responseBody[0].addressPhoneNumbers.first().contactType).isEqualTo("HOME")
      assertThat(responseBody[0].addressPhoneNumbers.first().contactValue).isEqualTo("012345678")
      assertThat(responseBody[0].addressPhoneNumbers.first().contactPhoneExtension).isEqualTo("567")
    }

    @Test
    fun `returns an empty list when a person is found with no addresses`() {
      stubAddressApi(emptyList())

      val response = underTest.getAddresses(PERSON_ID)
      assertThat(response.statusCode.is2xxSuccessful).isTrue()

      val responseBody = response.body!!
      assertThat(responseBody.size).isEqualTo(0)
    }

    @Test
    fun `returns the status from the API when it returns unsuccessful`() {
      whenever(prisonApiClient.getAddresses(PERSON_ID)).thenReturn(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build())

      val response = underTest.getAddresses(PERSON_ID)
      assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_GATEWAY)
    }
  }

  @Nested
  inner class CreateAddress {
    private fun stubAddressApi(address: ResponseEntity<AddressPrisonDto>) {
      whenever(prisonApiClient.createAddress(eq(PERSON_ID), any())).thenReturn(address)
    }

    @Test
    fun `Creates and returns address`() {
      stubAddressApi(ResponseEntity.ok(PRISON_ADDRESS_1))

      val response =
        underTest.createAddress(
          PERSON_ID,
          AddressRequestDto(
            buildingNumber = "1",
            subBuildingName = "Flat 1",
            buildingName = "The Building",
            thoroughfareName = "The Road",
            dependantLocality = "The Locality",
            postTownCode = "TOWN1",
            countyCode = "COUNTY1",
            countryCode = "COUNTRY1",
            postCode = "A1 2BC",
            primaryAddress = true,
            postalAddress = true,
            noFixedAbode = true,
            fromDate = LocalDate.parse("2021-01-01"),
            toDate = LocalDate.parse("2022-02-02"),
            addressTypes = listOf("HOME"),
          ),
        )

      assertThat(response.statusCode.is2xxSuccessful).isTrue()
      assertThat(response.body!!.addressId).isEqualTo(PRISON_ADDRESS_1.addressId)

      verify(prisonApiClient).createAddress(
        PERSON_ID,
        CreateAddress(
          flat = "1",
          premise = "Flat 1 The Building",
          street = "The Road",
          locality = "The Locality",
          townCode = "TOWN1",
          countyCode = "COUNTY1",
          countryCode = "COUNTRY1",
          postalCode = "A1 2BC",
          primary = true,
          mail = true,
          noFixedAddress = true,
          startDate = LocalDate.parse("2021-01-01"),
          endDate = LocalDate.parse("2022-02-02"),
          addressUsages = listOf("HOME"),
        ),
      )
    }

    @Test
    fun `Handles null address fields correctly`() {
      stubAddressApi(ResponseEntity.ok(PRISON_ADDRESS_1))

      val response =
        underTest.createAddress(PERSON_ID, AddressRequestDto(countryCode = "ENG", fromDate = LocalDate.parse("2021-01-01")))

      assertThat(response.statusCode.is2xxSuccessful).isTrue()
      assertThat(response.body!!.addressId).isEqualTo(PRISON_ADDRESS_1.addressId)

      verify(prisonApiClient).createAddress(
        PERSON_ID,
        CreateAddress(
          startDate = LocalDate.parse("2021-01-01"),
          countryCode = "ENG",
          addressUsages = emptyList(),
        ),
      )
    }

    @Test
    fun `Returns the status from the API when it returns unsuccessful`() {
      stubAddressApi(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())

      val response =
        underTest.createAddress(PERSON_ID, AddressRequestDto(countryCode = "ENG", fromDate = LocalDate.now()))

      assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  private companion object {
    const val PERSON_ID = "ABC123"

    val PRISON_ADDRESS_1 = AddressPrisonDto(
      addressId = 123,
      addressType = null,
      flat = "1",
      premise = "The Building",
      street = "The Road",
      locality = "The Locality",
      town = "My Town",
      townCode = "TOWN1",
      county = "My County",
      countyCode = "COUNTY1",
      country = "My Country",
      countryCode = "COUNTRY1",
      postalCode = "A1 2BC",
      primary = true,
      mail = true,
      noFixedAddress = true,
      startDate = LocalDate.parse("2021-01-01"),
      endDate = LocalDate.parse("2022-02-02"),
      comment = "Some comment",
      addressUsages = listOf(AddressUsage(111, "HOME", "Home", true)),
      phones = listOf(Telephone(222, "012345678", "HOME", "567")),
    )
  }
}
