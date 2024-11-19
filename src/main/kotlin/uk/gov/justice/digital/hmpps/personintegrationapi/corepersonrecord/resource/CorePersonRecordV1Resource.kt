package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.CorePersonRecordService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Core Person Record V1",
  description = "Core information for a HMPPS person.",
)
@RequestMapping(value = ["v1/core-person-record"])
class CorePersonRecordV1Resource(
  private val corePersonRecordService: CorePersonRecordService,
) {

  @PatchMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Performs partial updates on the core person record by prisoner number",
    description = "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "The core person record data has been patched successfully.",
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
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}",
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
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}')")
  fun patchByPrisonerNumber(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid corePersonRecordUpdateRequest: CorePersonRecordV1UpdateRequestDto,
  ): ResponseEntity<Void> {
    corePersonRecordService.updateCorePersonRecordField(
      prisonerNumber,
      corePersonRecordUpdateRequest.fieldName,
      corePersonRecordUpdateRequest.fieldValue,
    )

    return ResponseEntity.noContent().build()
  }

  @PutMapping(
    "/profile-image",
    consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    produces = [
      MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE,
    ],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Add or updates the profile image on the core person record by prisoner number",
    description = "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "The image file has been uploaded successfully.",
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Data not found.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_WRITE_ROLE}')")
  fun putProfileImageByPrisonerNumber(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestPart(name = "Image file", required = true) profileImage: MultipartFile,
  ): ResponseEntity<InputStreamResource> {
    val inputStreamResource = InputStreamResource(profileImage.inputStream)
    return ok().contentType(
      MediaType.parseMediaType(
        profileImage.contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE,
      ),
    ).contentLength(profileImage.size).header(
      HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=\"${profileImage.originalFilename}\"",
    ).body(inputStreamResource)
  }

  @GetMapping("reference-data/domain/{domain}/codes")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get all reference data codes for the given domain",
    description = "Returns the list of reference data codes within the given domain. " +
      "This endpoint only returns active reference data codes. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}`",
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
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}')")
  fun getReferenceDataCodesByDomain(
    @PathVariable @Schema(
      description = "The reference data domain",
      example = "COUNTRY",
    ) domain: String,
  ): Collection<ReferenceDataCodeDto> = listOf()
}
