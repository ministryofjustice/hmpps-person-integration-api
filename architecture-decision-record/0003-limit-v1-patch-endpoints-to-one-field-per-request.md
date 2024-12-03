[< Previous](0002-include-username-in-client-credential-token.md),
[Contents](README.md),
[Next >](9999-end.md)



# 3. Limit V1 patch endpoints to updating one field per request

Date: 2024-11-28

## Status

✅ Accepted

## Context

For version 1 endpoints the HMPPS Person Integration API will make calls to the Prison API to update the source data in NOMIS. For simplicity the 
endpoints on the Prison API update a single field per request. Therefore, if the Person Integration API were to allow multiple fields to be updated in
a single request this would require multiple calls to the Prison API. If one of these calls were to fail then we would need to roll back the changes
from any successful calls in order to leave the data in a consistent state. This could be handled with retries and a fallback to reverse the updates
however this will add complexity without a valuable user case for doing so.

## Decision

For simplicity the version 1 patch endpoints will allow a single field per-request while the Prison API is being used as the underlying datasource.

## Consequences
N/A

