package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.PseudonymRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PseudonymResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.PseudonymService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Pseudonym V1",
  description = "Pseudonyms for a HMPPS person.",
)
@Validated
@RequestMapping(value = ["v1/pseudonym"])
class PseudonymV1Resource(
  private val pseudonymService: PseudonymService,
) {
  @PostMapping
  @Operation(
    summary = "Create a new pseudonym for the given prisoner number.",
    description = "Creates a pseudonym. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Pseudonym successfully created.",
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
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Prisoner number not found",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun createPseudonym(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestParam(required = true) @NotNull sourceSystem: String,
    @RequestBody(required = true) @Valid pseudonymRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> = pseudonymService.createPseudonym(prisonerNumber, sourceSystem, pseudonymRequest)

  @PutMapping("/{pseudonymId}")
  @Operation(
    summary = "Update a pseudonym for the given pseudonym ID. Whilst proxying, this will be the `offenderId` in NOMIS.",
    description = "Updates a pseudonym. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Pseudonym successfully updated.",
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
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Pseudonym not found",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun updatePseudonym(
    @PathVariable pseudonymId: Long,
    @RequestParam(required = true) @NotNull sourceSystem: String,
    @RequestBody(required = true) @Valid pseudonymRequest: PseudonymRequestDto,
  ): ResponseEntity<PseudonymResponseDto> = pseudonymService.updatePseudonym(pseudonymId, sourceSystem, pseudonymRequest)
}
