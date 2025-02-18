package uk.gov.justice.digital.hmpps.personintegrationapi.common.resolver

import org.springframework.core.MethodParameter
import org.springframework.web.service.invoker.HttpMethodArgumentResolver
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpRequestValues.Builder
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest

class DistinguishingMarkCreateRequestResolver : HttpMethodArgumentResolver() {

  override fun resolve(argument: Any?, parameter: MethodParameter, requestValues: HttpRequestValues.Builder): Boolean {
    if (argument != null && argument is DistinguishingMarkCreateRequest) {
      addRequestPart(requestValues, "markType", argument.markType)
      addRequestPart(requestValues, "bodyPart", argument.bodyPart)
      addRequestPart(requestValues, "side", argument.side)
      addRequestPart(requestValues, "partOrientation", argument.partOrientation)
      addRequestPart(requestValues, "comment", argument.comment)
      return true
    }
    return false
  }

  private fun addRequestPart(requestValues: Builder, parameter: String, value: Any?) {
    if (value != null) {
      requestValues.addRequestPart(parameter, value)
    }
  }
}
