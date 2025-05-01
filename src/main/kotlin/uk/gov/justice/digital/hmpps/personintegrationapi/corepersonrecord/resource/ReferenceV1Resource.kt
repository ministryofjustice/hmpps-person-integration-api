package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.ReferenceRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.ReferenceResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.ReferenceService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Reference V1",
  description = "References (Identifiers) for a HMPPS person.",
)
@Validated
@RequestMapping(value = ["v1/person/{personId}"], produces = [APPLICATION_JSON_VALUE])
class ReferenceV1Resource(
  private val referenceService: ReferenceService,
) {

  @GetMapping("/reference")
  @Operation(
    summary = "Returns list of reference identifiers for the given person.",
    description = "Get references. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or " +
      "`${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}` ",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
      ),
      Parameter(
        name = "includePseudonyms",
        description = "Boolean indicating whether to include references for all pseudonyms. If false only references for the working name will be returned.",
        example = "false",
      ),
    ],
    responses = [
      ApiResponse(responseCode = "200", description = "Successful response."),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires either ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} " +
          "or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Person not found",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getReferences(
    @PathVariable @Valid @ValidPrisonerNumber personId: String,
    @RequestParam(required = false, defaultValue = false.toString()) includePseudonyms: Boolean,
  ): ResponseEntity<Collection<ReferenceResponseDto>> = referenceService.getReferences(personId, includePseudonyms)

  @PostMapping("/reference")
  @Operation(
    summary = "Create a new reference for the given person.",
    description = "Creates a reference. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
      ),
    ],
    responses = [
      ApiResponse(responseCode = "201", description = "Reference successfully created."),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Person not found",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun createReference(
    @PathVariable(required = true) @Valid @ValidPrisonerNumber personId: String,
    @RequestBody(required = true) @Valid referenceRequest: ReferenceRequestDto,
  ): ResponseEntity<ReferenceResponseDto> = referenceService.createReference(personId, referenceRequest)

  @PutMapping("/references/{referenceId}")
  @Operation(
    summary = "Update a reference for the given reference and person ID.",
    description = "Updates a reference. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
      ),
      Parameter(
        name = "referenceId",
        description = "The identifier for the reference being updated. While NOMIS is the underlying datasource this value will be the offender id seq",
        example = "12345",
      ),
    ],
    responses = [
      ApiResponse(responseCode = "200", description = "Reference successfully updated."),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Person or reference not found",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun updateReference(
    @PathVariable(required = true) @Valid @ValidPrisonerNumber personId: String,
    @PathVariable referenceId: Long,
    @RequestBody(required = true) @Valid referenceRequest: ReferenceRequestDto,
  ): ResponseEntity<ReferenceResponseDto> = referenceService.updateReference(personId, referenceId, referenceRequest)
}
