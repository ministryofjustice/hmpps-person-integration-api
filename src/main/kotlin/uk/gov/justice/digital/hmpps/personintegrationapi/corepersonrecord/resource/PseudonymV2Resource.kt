package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.PseudonymRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.PseudonymService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Pseudonym V2",
  description = "Pseudonyms for a HMPPS person.",
)
@Validated
@RequestMapping(value = ["v2/person/{personId}"], produces = [APPLICATION_JSON_VALUE])
class PseudonymV2Resource(
  private val pseudonymService: PseudonymService,
) {

  @GetMapping("/pseudonyms")
  @Operation(
    summary = "Returns list of pseudonyms for the given prisoner number.",
    description = "Get pseudonyms. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or " +
      "`${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}` ",
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
        description = "Prisoner number not found",
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
  fun getPseudonyms(
    @PathVariable personId: String,
  ): ResponseEntity<List<PseudonymResponseDto>> = pseudonymService.getPseudonyms(personId)

  @PostMapping("/pseudonym")
  @Operation(
    summary = "Create a new pseudonym for the given prisoner number.",
    description = "Creates a pseudonym. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(responseCode = "201", description = "Pseudonym successfully created."),
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
        description = "Prisoner number not found",
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
  fun createPseudonym(
    @PathVariable personId: String,
    @RequestBody(required = true) @Valid pseudonymRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> = pseudonymService.createPseudonym(personId, "REMOVE_ME_FOR_V2", pseudonymRequest)

  @PutMapping("/pseudonym/{pseudonymId}")
  @Operation(
    summary = "Update a pseudonym for the given pseudonym ID. Whilst proxying, this will be the `offenderId` in NOMIS.",
    description = "Updates a pseudonym. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(responseCode = "200", description = "Pseudonym successfully updated."),
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
        description = "Pseudonym not found",
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
  fun updatePseudonym(
    @PathVariable personId: String,
    @PathVariable pseudonymId: Long,
    @RequestBody(required = true) @Valid pseudonymRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> = pseudonymService.updatePseudonym(pseudonymId, "REMOVE_ME_FOR_V2", pseudonymRequest)
}
