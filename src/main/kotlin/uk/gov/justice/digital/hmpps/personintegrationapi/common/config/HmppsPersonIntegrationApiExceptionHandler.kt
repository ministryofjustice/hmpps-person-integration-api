package uk.gov.justice.digital.hmpps.personintegrationapi.common.config

import jakarta.validation.ValidationException
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.LOCKED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.servlet.resource.NoResourceFoundException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.DuplicateIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.IdentifierNotFoundException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierTypeException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.VirusScanFailureException
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.stream.Collectors

@RestControllerAdvice
class HmppsPersonIntegrationApiExceptionHandler {
  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
    val type = e.requiredType
    val message = if (type.isEnum) {
      "Parameter ${e.name} must be one of the following ${
        StringUtils.join(
          type.enumConstants,
          ", ",
        )
      }"
    } else {
      "Parameter ${e.name} must be of type ${type.typeName}"
    }

    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Validation failure: $message",
          developerMessage = e.message,
        ),
      ).also { log.info("Method argument type mismatch exception: {}", e.message) }
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleValidationException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = "Unable to read the request",
        developerMessage = ex.message,
      ),
    ).also { log.info(ex.message) }

  @ExceptionHandler(MissingServletRequestParameterException::class)
  fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = "Missing parameter: ${ex.parameterName}",
        developerMessage = ex.message,
      ),
    ).also { log.info(ex.message) }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = e.bindingResult.fieldErrors
          .stream()
          .map { error: FieldError -> "Field: " + error.field + " - " + error.defaultMessage }
          .collect(Collectors.joining(", ")),
        developerMessage = e.message,
      ),
    ).also { log.info("Validation exception: {}", e.message) }

  @ExceptionHandler(HandlerMethodValidationException::class)
  fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ResponseEntity<ErrorResponse> = e.allErrors.map { it.toString() }.distinct().sorted().joinToString("\n")
    .let { validationErrors ->
      ResponseEntity
        .status(BAD_REQUEST)
        .body(
          ErrorResponse(
            status = BAD_REQUEST,
            userMessage = "Validation failure(s): ${
              e.allErrors.map { it.defaultMessage }.distinct().sorted().joinToString("\n")
            }",
            developerMessage = "${e.message} $validationErrors",
          ),
        ).also { log.info("Validation exception: $validationErrors\n {}", e.message) }
    }

  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(e: ValidationException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = "Validation failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("Validation exception: {}", e.message) }

  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(NOT_FOUND)
    .body(
      ErrorResponse(
        status = NOT_FOUND,
        userMessage = "No resource found failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("No resource found exception: {}", e.message) }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(FORBIDDEN)
    .body(
      ErrorResponse(
        status = FORBIDDEN,
        userMessage = "Forbidden: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.debug("Forbidden (403) returned: {}", e.message) }

  @ExceptionHandler(VirusScanException::class)
  fun handleVirusScanException(e: VirusScanException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(INTERNAL_SERVER_ERROR)
    .body(
      ErrorResponse(
        status = INTERNAL_SERVER_ERROR,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Internal server error (500) returned: {}", e.message) }

  @ExceptionHandler(VirusScanFailureException::class)
  fun handleVirusScanFailureException(e: VirusScanFailureException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Bad request (400) returned due to virus scan: {}", e.message) }

  @ExceptionHandler(IdentifierNotFoundException::class)
  fun handleIdentifierNotFoundException(e: IdentifierNotFoundException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(NOT_FOUND)
    .body(
      ErrorResponse(
        status = NOT_FOUND,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Not found (404) returned due to existing identifier not found: {}", e.message) }

  @ExceptionHandler(InvalidIdentifierException::class)
  fun handleInvalidIdentifierException(e: InvalidIdentifierException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Bad request (400) returned due to invalid identifier value: {}", e.message) }

  @ExceptionHandler(DuplicateIdentifierException::class)
  fun handleDuplicateIdentifierException(e: DuplicateIdentifierException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Bad request (400) returned due to duplicate identifier value: {}", e.message) }

  @ExceptionHandler(InvalidIdentifierTypeException::class)
  fun handleInvalidIdentifierTypeException(e: InvalidIdentifierTypeException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = e.message,
        developerMessage = e.message,
      ),
    ).also { log.debug("Bad request (400) returned due to invalid identifier type value: {}", e.message) }

  @ExceptionHandler(Exception::class)
  fun handleException(e: Exception): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(INTERNAL_SERVER_ERROR)
    .body(
      ErrorResponse(
        status = INTERNAL_SERVER_ERROR,
        userMessage = "Unexpected error: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.error("Unexpected exception", e) }

  @ExceptionHandler(WebClientResponseException::class)
  fun handleException(e: WebClientResponseException): ResponseEntity<ErrorResponse> {
    return if (e.statusCode == LOCKED) {
      ResponseEntity
        .status(LOCKED)
        .body(
          ErrorResponse(
            status = LOCKED,
            userMessage = "Resource locked: ${e.message}",
            developerMessage = e.message,
          ),
        )
    } else {
      ResponseEntity
        .status(e.statusCode)
        .body(e.getResponseBodyAs(ErrorResponse::class.java))
        .also { log.debug("Exception during call to client", e) }
    }
  }

  private companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
