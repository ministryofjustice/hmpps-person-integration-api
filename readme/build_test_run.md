[< Back](../README.md)
---

## Building

To use the same version of Java locally as is used in CI and production, follow [these notes](sdkman.md).

To build the project without tests run:

```
./gradlew clean build -x test
```

## Testing

To run the unit and integration tests:
```
./gradlew test 
```

## Running Locally

There are two variation for running the application locally.

- [Running against containerised dependencies](#Running against containerised dependencies)
- [Running against dependencies deployed in dev](#Running against dependencies deployed in dev)

### Running against containerised dependencies

The following command will build and run the application via docker compose along with containers for HMPPS Auth and Prison API.

> NOTE: A client with the required roles will need to be added to the containerised version of HMPPS Auth in order to generate OAuth tokens.
> The client id and secret can then be referenced in a .env file as the `SYSTEM_CLIENT_ID` and `SYSTEM_CLIENT_SECRET`.

```
docker compose pull && docker compose up --build
```

This will run the application on http://localhost:8080 and the swagger docs will be found at http://localhost:8080/swagger-ui/index.html# .

#### Running through Gradle or IntelliJ

If you want to run the application through IntelliJ or command line with gradle using the containerised dependencies then use:

```
docker compose pull && docker compose up --scale hmpps-person-integration-api=0 
```  

Then start the application using Gradle or IntelliJ.

> NOTE: The required client credentials environment variables (`SYSTEM_CLIENT_ID` and `SYSTEM_CLIENT_SECRET`) will need to set prior to starting the application.

**IntelliJ:**

Run or debug the main class with the spring active profile set to `dev`:

**Gradle:**

```
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Running against dependencies deployed in dev

In order to run against deployed dependencies in dev the 

<details>
<summary>Environment variables required</summary>
<br>
Note, client credentials from the dev namespace (hmpps-person-integration-api-dev) kubernetes secrets.

```
SYSTEM_CLIENT_ID=<Extract from k8s namespace>
SYSTEM_CLIENT_SECRET=<Extract from k8s namespace>
HMPPS_AUTH_URL=https://sign-in-dev.hmpps.service.justice.gov.uk/auth
PRISON_API_BASE_URL=https://prison-api-dev.prison.service.justice.gov.uk
```
</details>

Once the environment variables have been set the application can be run via Gradle or IntelliJ using the commands in [this section](#running-through-gradle-or-intellij)

## Common gradle tasks

To list project dependencies, run:

```
./gradlew dependencies
```

To check for dependency updates, run:
```
./gradlew dependencyUpdates --warning-mode all
```

To run an OWASP dependency check, run:
```
./gradlew clean dependencyCheckAnalyze --info
```

To upgrade the gradle wrapper version, run:
```
./gradlew wrapper --gradle-version=<VERSION>
```

To automatically update project dependencies, run:
```
./gradlew useLatestVersions
```

#### Ktlint Gradle Tasks

To run Ktlint check:
```
./gradlew ktlintCheck
```

To run Ktlint format:
```
./gradlew ktlintFormat
```

To register pre-commit check to run Ktlint format:
```
./gradlew addKtlintFormatGitPreCommitHook 
```

...or to register pre-commit check to only run Ktlint check:
```
./gradlew addKtlintCheckGitPreCommitHook
```