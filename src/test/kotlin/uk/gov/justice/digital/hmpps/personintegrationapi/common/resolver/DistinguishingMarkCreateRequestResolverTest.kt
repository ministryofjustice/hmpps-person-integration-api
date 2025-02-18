package uk.gov.justice.digital.hmpps.personintegrationapi.common.resolver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.springframework.core.MethodParameter
import org.springframework.web.service.invoker.HttpRequestValues
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest

@ExtendWith(MockitoExtension::class)
class DistinguishingMarkCreateRequestResolverTest {

  @Test
  fun `maps supported request correctly`() {
    val methodParameter: MethodParameter = mock()
    val requestBuilder: HttpRequestValues.Builder = spy()
    val resolver = DistinguishingMarkCreateRequestResolver()
    val request = DistinguishingMarkCreateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Some comment",
    )

    assertThat(resolver.resolve(request, methodParameter, requestBuilder)).isTrue()

    verify(requestBuilder).addRequestPart("bodyPart", "TORSO")
    verify(requestBuilder).addRequestPart("markType", "SCAR")
    verify(requestBuilder).addRequestPart("side", "L")
    verify(requestBuilder).addRequestPart("partOrientation", "UPP")
    verify(requestBuilder).addRequestPart("comment", "Some comment")
  }

  @Test
  fun `Does not map null values into request`() {
    val methodParameter: MethodParameter = mock()
    val requestBuilder: HttpRequestValues.Builder = spy()
    val resolver = DistinguishingMarkCreateRequestResolver()
    val request = DistinguishingMarkCreateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
    )

    assertThat(resolver.resolve(request, methodParameter, requestBuilder)).isTrue()

    verify(requestBuilder).addRequestPart("bodyPart", "TORSO")
    verify(requestBuilder).addRequestPart("markType", "SCAR")
    verify(requestBuilder, never()).addRequestPart(eq("side"), any())
    verify(requestBuilder, never()).addRequestPart(eq("partOrientation"), any())
    verify(requestBuilder, never()).addRequestPart(eq("comment"), any())
  }

  @Test
  fun `Does not map unsupported object types`() {
    val methodParameter: MethodParameter = mock()
    val requestBuilder: HttpRequestValues.Builder = spy()
    val resolver = DistinguishingMarkCreateRequestResolver()
    val request = DistinguishingMarkUpdateRequest(
      bodyPart = "TORSO",
      markType = "SCAR",
      side = "L",
      partOrientation = "UPP",
      comment = "Some comment",
    )

    assertThat(resolver.resolve(request, methodParameter, requestBuilder)).isFalse()
    verifyNoInteractions(requestBuilder)
  }
}
