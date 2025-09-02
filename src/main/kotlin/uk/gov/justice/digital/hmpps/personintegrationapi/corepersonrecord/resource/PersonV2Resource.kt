import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.CorePersonRecordRoleConstants
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.FullPersonResponseDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service.PersonService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@Tag(
  name = "Core Person Record V2",
  description = "Core information for a HMPPS person.",
)
@RequestMapping(value = ["v2"])
class CorePersonRecordV2Resource(
  private val personService: PersonService,
) {
  @GetMapping("/person/{personId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Get all information held about a particular prisoner.",
    description = "Returns data available from /person/{personId}/... endpoints exposed by this API. " +
      "Requires role `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_ROLE}` or `${CorePersonRecordRoleConstants.CORE_PERSON_RECORD_READ_WRITE_ROLE}`",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Prisoner data found",
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
  fun getPersonById(
    @PathVariable personId: String,
  ): ResponseEntity<FullPersonResponseDto> {
    return personService.getPerson(personId)
  }
}