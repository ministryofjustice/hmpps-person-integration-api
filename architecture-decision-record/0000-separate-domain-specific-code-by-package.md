[Contents](README.md),
[Next >](0001-structure-packages-by-api-version.md)

# 0. Separate domain specific code by package

Date: 2024-11-21

## Status

✅ Accepted

## Context

The HMPPS Person integration API provides an interface to access data from two different domains.
The `Core Person Record` domain and the `Person Protected Characteristics` domain. These domains are
related but may be separated out into separate services in the future.

## Decision

There will be a top level package for each domain, `corepersonrecord` and `personprotectedcharacteristics` which will keep
any domain specific logic separate. A `common` package will be used for any shared code.

```
project/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── common/                         # Common code shared across domains
│   │   │   ├── corepersonrecord/               # Core person record package
│   │   │   ├── personprotectedcharacteristics/ # Person protected characteristics package
│   │   └── resources/
```

## Consequences

N/A
