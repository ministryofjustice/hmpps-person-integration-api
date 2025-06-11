package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.resource

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
import org.springframework.web.bind.annotation.PatchMapping
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
import uk.gov.justice.digital.hmpps.personintegrationapi.common.annotation.ValidPrisonerNumber
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.CreateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.UpdateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Performs partial updates on the core person record by prisoner number",
    description = "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
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
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun patchByPrisonerNumber(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid corePersonRecordUpdateRequest: CorePersonRecordV1UpdateRequestDto,
  ): ResponseEntity<Void> {
    corePersonRecordService.updateCorePersonRecordField(prisonerNumber, corePersonRecordUpdateRequest)
    return noContent().build()
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Add or updates the profile image on the core person record by prisoner number",
    description = "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
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
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
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
  @PreAuthorize("hasRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun putProfileImageByPrisonerNumber(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestPart(name = "imageFile", required = true) profileImage: MultipartFile,
  ): ResponseEntity<Void> = corePersonRecordService.updateProfileImage(profileImage, prisonerNumber)

  @GetMapping("reference-data/domain/{domain}/codes")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get all reference data codes for the given domain",
    description = "Returns the list of reference data codes within the given domain. " +
      "This endpoint only returns active reference data codes. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
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
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Not found, the reference data domain was not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getReferenceDataCodesByDomain(
    @PathVariable @Schema(
      description = "The reference data domain",
      example = "COUNTRY",
    ) domain: String,
  ): ResponseEntity<List<ReferenceDataCodeDto>> = corePersonRecordService.getReferenceDataCodes(domain)

  @GetMapping("military-records")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get military records for the given prisoner number",
    description = "Returns the list of military records for the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Military records found",
        content = [Content(array = ArraySchema(schema = Schema(implementation = MilitaryRecordDto::class)))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Prisoner not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getMilitaryRecords(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
  ): ResponseEntity<List<MilitaryRecordDto>> = corePersonRecordService.getMilitaryRecords(prisonerNumber)

  @PutMapping("/military-records")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Update military record for the given prisoner number",
    description = "Updates a military record. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "Military record successfully updated.",
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
        description = "Military record not found",
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
  fun putMilitaryRecord(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestParam(required = true) militarySeq: Int,
    @RequestBody(required = true) @Valid militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void> = corePersonRecordService.updateMilitaryRecord(prisonerNumber, militarySeq, militaryRecordRequest)

  @PostMapping("/military-records")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create military record for the given prisoner number",
    description = "Creates a military record. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Military record successfully created.",
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
        description = "Record not found",
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
  fun postMilitaryRecord(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid militaryRecordRequest: MilitaryRecordRequest,
  ): ResponseEntity<Void> = corePersonRecordService.createMilitaryRecord(prisonerNumber, militaryRecordRequest)

  @PutMapping("/nationality")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Update the nationality for a given prisoner number",
    description = "Updates the nationality and other nationalities info. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "Nationality successfully updated.",
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
        description = "Record not found",
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
  fun updateNationality(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid updateNationality: UpdateNationality,
  ): ResponseEntity<Void> = corePersonRecordService.updateNationality(prisonerNumber, updateNationality)

  @GetMapping("/physical-attributes")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Get physical attributes for the given prisoner number",
    description = "Returns the physical attributes for the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Physical attributes found",
        content = [Content(schema = Schema(implementation = PhysicalAttributesDto::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Missing required role. Requires ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE} or ${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}.",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Prisoner not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}', '${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}')")
  fun getPhysicalAttributes(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
  ): ResponseEntity<PhysicalAttributesDto> = corePersonRecordService.getPhysicalAttributes(prisonerNumber)

  @PutMapping("/physical-attributes")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Update physical attributes for the given prisoner number",
    description = "Updates the physical attributes for the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "Physical attributes successfully updated.",
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
        description = "Record not found",
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
  fun updatePhysicalAttributes(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid physicalAttributesRequest: PhysicalAttributesRequest,
  ): ResponseEntity<Void> = corePersonRecordService.updatePhysicalAttributes(prisonerNumber, physicalAttributesRequest)

  @PutMapping("/identifiers")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Update an identifier for the given prisoner number and sequence id",
    description = "Updates the identifier for the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "204",
        description = "Identifier successfully updated.",
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
        description = "Identifier not found",
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
  fun updateIdentifier(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestParam(required = true) seqId: Long,
    @RequestBody(required = true) @Valid request: UpdateIdentifierRequestDto,
  ): ResponseEntity<Void> = corePersonRecordService.updateIdentifier(prisonerNumber, seqId, request)

  @PostMapping("/identifiers")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Adds one or more identifiers to the prisoner with the given prisoner number",
    description = "Adds identifiers to the prisoner. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Identifiers successfully added.",
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
        description = "Prisoner not found",
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
  fun addIdentifiers(
    @RequestParam(required = true) @Valid @ValidPrisonerNumber prisonerNumber: String,
    @RequestBody(required = true) @Valid createRequests: List<CreateIdentifierRequestDto>,
  ): ResponseEntity<Void> = corePersonRecordService.addIdentifiers(prisonerNumber, createRequests)
}
