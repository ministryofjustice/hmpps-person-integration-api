package uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.DocumentApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.PrisonApiClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.ReferenceDataClient
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.CreateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.MilitaryRecordRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.PhysicalAttributesRequest
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthCountry
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateBirthPlace
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateNationality
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.request.UpdateSexualOrientation
import uk.gov.justice.digital.hmpps.personintegrationapi.common.client.response.IdentifierPrisonDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.dto.ReferenceDataCodeDto
import uk.gov.justice.digital.hmpps.personintegrationapi.common.mapper.mapRefDataDescription
import uk.gov.justice.digital.hmpps.personintegrationapi.common.util.virusScan
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.CreateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.request.UpdateIdentifierRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.MilitaryRecordDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.response.PhysicalAttributesDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.BirthplaceUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CorePersonRecordV1UpdateRequestDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.CountryOfBirthUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.dto.v1.request.SexualOrientationUpdateDto
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.DuplicateIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.IdentifierNotFoundException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.InvalidIdentifierTypeException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.exception.UnknownCorePersonFieldException
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers.CROIdentifier
import uk.gov.justice.digital.hmpps.personintegrationapi.corepersonrecord.identifiers.PNCIdentifier

@Service
class PersonService(
  private val prisonApiClient: PrisonApiClient,
  private val referenceDataClient: ReferenceDataClient,
  private val documentApiClient: DocumentApiClient,
) {
  fun getPerson(prisonerNumber: String): ResponseEntity<PhysicalAttributesDto> {
    val personAsPrisonDto = prisonApiClient.getFullPerson(prisonerNumber)



    if (!response.statusCode.is2xxSuccessful) return ResponseEntity.status(response.statusCode).build()

    val mappedResponse = response.body?.let { body ->
      PhysicalAttributesDto(
        height = body.height,
        weight = body.weight,
        hair = body.hair?.toReferenceDataValue(),
        facialHair = body.facialHair?.toReferenceDataValue(),
        face = body.face?.toReferenceDataValue(),
        build = body.build?.toReferenceDataValue(),
        leftEyeColour = body.leftEyeColour?.toReferenceDataValue(),
        rightEyeColour = body.rightEyeColour?.toReferenceDataValue(),
        shoeSize = body.shoeSize,
      )
    }

    return ResponseEntity.ok(mappedResponse)
  }
}
