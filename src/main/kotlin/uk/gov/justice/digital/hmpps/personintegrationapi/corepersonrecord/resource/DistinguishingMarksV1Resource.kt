package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkCreateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.DistinguishingMarkUpdateRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.DistinguishingMarkDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.DistinguishingMarksService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Distinguishing Marks V1",
  description = "Distinguishing marks information and images for a HMPPS person.",
)
@RequestMapping(value = ["v1"])
class DistinguishingMarksV1Resource(
  private val distinguishingMarksService: DistinguishingMarksService,
) {

  @GetMapping(
    "/distinguishing-marks",
    produces = [APPLICATION_JSON_VALUE],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get details of all distinguishing marks associated with a prisoner's latest booking",
    description = "Returns the list of distinguishing marks for the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "prisonerNumber",
        description = "The unique prisoner reference code.",
        example = "A1234AA",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing marks found",
        content = [Content(array = ArraySchema(schema = Schema(implementation = DistinguishingMarkDto::class)))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Prisoner not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getDistinguishingMark(
    @RequestParam(required = true) prisonerNumber: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<List<DistinguishingMarkDto>> = distinguishingMarksService.getDistinguishingMarks(prisonerNumber, sourceSystem)

  @GetMapping(
    "/distinguishing-mark/{markId}",
    produces = [APPLICATION_JSON_VALUE],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get details of a distinguishing mark",
    description = "Returns the specified distinguishing mark. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "markId",
        description = "The mark identifier, which is a combination of the prisoner number and mark sequence id separated with a hyphen.",
        example = "A1234AA-1",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing mark found",
        content = [Content(schema = Schema(implementation = DistinguishingMarkDto::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Distinguishing mark not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getDistinguishingMarks(
    @PathVariable(required = true) markId: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = distinguishingMarksService.getDistinguishingMark(markId, sourceSystem)

  @PutMapping(
    "/distinguishing-mark/{markId}",
    produces = [APPLICATION_JSON_VALUE],
    consumes = [APPLICATION_JSON_VALUE],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Update an existing distinguishing mark",
    description = "Returns the updated distinguishing mark. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "markId",
        description = "The mark identifier, which is a combination of the prisoner number and mark sequence id separated with a hyphen.",
        example = "A1234AA-1",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing mark created",
        content = [Content(schema = Schema(implementation = DistinguishingMarkDto::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Distinguishing mark not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun updateDistinguishingMark(
    @RequestBody(required = true) @Valid request: DistinguishingMarkUpdateRequest,
    @PathVariable(required = true) markId: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = distinguishingMarksService.updateDistinguishingMark(request, markId, sourceSystem)

  @PostMapping(
    "/distinguishing-mark",
    produces = [APPLICATION_JSON_VALUE],
    consumes = [MULTIPART_FORM_DATA_VALUE],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Create a new distinguishing mark, optionally providing an image",
    description = "Returns the newly created distinguishing mark. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "prisonerNumber",
        description = "The unique prisoner reference code.",
        example = "A1234AA",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing mark updated",
        content = [Content(schema = Schema(implementation = DistinguishingMarkDto::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Distinguishing mark not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun createDistinguishingMark(
    @RequestPart(name = "file", required = false) file: MultipartFile? = null,
    @ModelAttribute request: DistinguishingMarkCreateRequest,
    @RequestParam(required = true) prisonerNumber: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = distinguishingMarksService.createDistinguishingMark(file, request, prisonerNumber, sourceSystem)

  @GetMapping("/distinguishing-mark/image/{imageId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get the image for a distinguishing mark",
    description = "Returns the specified image for a distinguishing mark. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "imageId",
        description = "The image identifier.",
        example = "1",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing mark image found",
        content = [Content(mediaType = IMAGE_JPEG_VALUE)],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [
          Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Distinguishing mark image not found",
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
  fun getDistinguishingMarkImage(
    @PathVariable(required = true) imageId: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<ByteArray> = distinguishingMarksService.getDistinguishingMarkImage(imageId, sourceSystem)

  @PostMapping(
    "/distinguishing-mark/{markId}/image",
    produces = [
      APPLICATION_JSON_VALUE,
    ],
    consumes = [
      MULTIPART_FORM_DATA_VALUE,
    ],
  )
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Add an image to an existing distinguishing mark",
    description = "Adds an image to an existing distinguishing mark. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    parameters = [
      Parameter(
        name = "markId",
        description = "The mark identifier, which is a combination of the prisoner number and mark sequence id separated with a hyphen.",
        example = "A1234AA-1",
      ),
      Parameter(
        name = "sourceSystem",
        description = "The source system which should be used to query the data.",
        example = "NOMIS",
      ),
    ],
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Distinguishing mark image added",
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Distinguishing mark image not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun addDistinguishingMarkImage(
    @RequestPart(name = "file", required = true) file: MultipartFile,
    @PathVariable(required = true) markId: String,
    @RequestParam(required = true) sourceSystem: String,
  ): ResponseEntity<DistinguishingMarkDto> = distinguishingMarksService.addDistinguishingMarkImage(file, markId, sourceSystem)
}
