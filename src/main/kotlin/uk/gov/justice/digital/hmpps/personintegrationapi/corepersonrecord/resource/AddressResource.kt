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
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.AddressRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.AddressResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.AddressService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Address V1",
  description = "Addresses for a HMPPS person.",
)
@Validated
@RequestMapping(value = ["v1/person/{personId}", "v2/person/{personId}"], produces = [APPLICATION_JSON_VALUE])
class AddressResource(
  private val addressService: AddressService,
) {

  @GetMapping("/addresses")
  @Operation(
    summary = "Returns list of addresses for the given person.",
    description = "Get addresses. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or " +
      "`${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}` ",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
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
  fun getAddresses(
    @PathVariable @Valid @ValidPrisonerNumber personId: String,
  ): ResponseEntity<Collection<AddressResponseDto>> = addressService.getAddresses(personId)

  @PostMapping("/addresses")
  @Operation(
    summary = "Create a new address for the given person.",
    description = "Creates an address. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
      ),
    ],
    responses = [
      ApiResponse(responseCode = "201", description = "Address successfully created."),
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
  fun createAddress(
    @PathVariable @Valid @ValidPrisonerNumber personId: String,
    @RequestBody(required = true) @Valid addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = addressService.createAddress(personId, addressRequestDto)

  @PutMapping("/addresses/{addressId}")
  @Operation(
    summary = "Update an address for the given person.",
    description = "Updates an address. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "personId",
        description = "The identifier for the person. While NOMIS is the underlying datasource this is the prisoner number",
        example = "A1234AA",
      ),
      Parameter(
        name = "addressId",
        description = "The address Id value.",
        example = "12345",
      ),
    ],
    responses = [
      ApiResponse(responseCode = "200", description = "Address successfully updated."),
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
        description = "Address not found",
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
  fun updateAddress(
    @PathVariable @Valid @ValidPrisonerNumber personId: String,
    @PathVariable addressId: Long,
    @RequestBody(required = true) @Valid addressRequestDto: AddressRequestDto,
  ): ResponseEntity<AddressResponseDto> = addressService.updateAddress(personId, addressId, addressRequestDto)
}
