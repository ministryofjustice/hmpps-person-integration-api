package uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectedcharacteristics.dto.v1.request.ReligionV1RequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.PersonProtectedCharacteristicsRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.personprotectercharacteristics.service.PersonProtectedCharacteristicsService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Person Protected characteristics V1",
  description = "Protected characteristics information for a HMPPS person.",
)
@RequestMapping(
  value = ["v1/person-protected-characteristics"],
  produces = [MediaType.APPLICATION_JSON_VALUE],
)
class PersonProtectedCharacteristicsV1Resource(
  private val personProtectedCharacteristicsService: PersonProtectedCharacteristicsService,
) {

  @PutMapping("/religion")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    description = "Requires role `${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "Religion data successfully added/updated.",
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE}",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Data not found",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE}')")
  fun putReligionByPrisonerNumber(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid religionV1RequestDto: ReligionV1RequestDto,
  ): ResponseEntity<Void> {
    personProtectedCharacteristicsService.updateReligion(prisonerNumber, religionV1RequestDto)
    return noContent().build()
  }

  @GetMapping("reference-data/domain/{domain}/codes")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get all reference data codes for the given domain",
    description = "Returns the list of reference data codes within the given domain. " +
      "This endpoint only returns active reference data codes. " +
      "Requires role `${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_ROLE}` or `${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Reference data codes found",
        content = [Content(array = ArraySchema(schema = Schema(implementation = ReferenceDataCodeDto::class)))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Not found, the reference data domain was not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_ROLE}', '${PersonProtectedCharacteristicsRoleConstants.PROTECTED_CHARACTERISTICS_READ_WRITE_ROLE}')")
  fun getReferenceDataCodesByDomain(
    @PathVariable @Schema(
      description = "The reference data domain",
      example = "COUNTRY",
    ) domain: String,
  ): Collection<ReferenceDataCodeDto> = listOf()
}
