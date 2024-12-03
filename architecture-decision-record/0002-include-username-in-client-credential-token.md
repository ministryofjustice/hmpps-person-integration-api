[< Previous](0001-structure-packages-by-api-version.md),
[Contents](README.md),
[Next >](0003-limit-v1-patch-endpoints-to-one-field-per-request.md)



# 2. Include username in client credentials token

Date: 2024-11-21

## Status

✅ Accepted

## Context

The HMPPS Person Integration API will make calls to downstream services using the OAuth 2.0 client credentials grant for authorisation.
The API will be authorised to make calls to downstream services however for audit purposes the username of the instigating user is required.
The HMPPS Auth service provides functionality to include a username within an OAuth token obtained using client credentials which can then be 
verified and extracted by downstream services.

## Decision

The API will make use of the HMPPS Auth functionality to include a username within the token generated whe using a client credentials grant.

## Consequences

In order to the HMPPS Auth functionality to include the username in token when using the client credentials grant will require a non-standard 
OAuth token request query.

