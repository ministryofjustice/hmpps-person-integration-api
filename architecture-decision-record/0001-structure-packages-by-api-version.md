[< Previous](0000-separate-domain-specific-code-by-package.md),
[Contents](README.md),
[Next >](0002-include-username-in-client-credential-token.md)

# 1. Structure packages by API version

Date: 2024-11-21

## Status

✅ Accepted

## Context

This API is likely to go through multiple iterations as data models change and expand. To ensure the
API can evolve without causing friction to clients, we will version the API whenever there are breaking changes.
As there may be multiple versions of the API live at a given time we need to code structured and easy to maintain and understand.

## Decision

Code specific to a specific API version will be stored in a separate package.

```
project/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── common/                # Common code shared across modules or components
│   │   │   ├── corepersonrecord/      # Core person record module
│   │   │   │   ├── shared/            # Code common between versions
│   │   │   │   ├── v1/                # Version 1 specific code
│   │   │   │   ├── v2/                # Version 2 specific code
│   │   └── resources/
```

## Consequences

We will need to refactor the current package structure when we start introducing version 2 functionality.
