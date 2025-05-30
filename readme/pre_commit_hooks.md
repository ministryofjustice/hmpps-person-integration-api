[< Back](../README.md)
---

## Pre-Commit Hooks

Before considering commiting to this project, you are strongly encouraged to set up pre-commit hooks as follows:

### Installing gitleaks

To prevent accidentally commiting secrets into this repository, first install gitleaks:

```shell
brew install gitleaks
```

### Setting up pre-commit for the first time

If you haven't installed [pre-commit](https://pre-commit.com/) before you can follow
the instructions on their website or on a Mac:

1. Install [pipx](https://pipx.pypa.io/stable/#on-macos)

```shell
brew install pipx
pipx ensurepath
sudo pipx ensurepath --global # optional to allow pipx actions with --global argument
```

2. Install the pre-commit framework
```shell
pipx install pre-commit
```

### Install the pre-commit hooks for this repository

Once you have pre-commit installed, in the root of your repo you can then run:

```shell
# 1. Install gitleaks pre-commit hook:
pre-commit install

# 2. Install ktlint pre-commit hook:
./gradlew addKtlintFormatGitPreCommitHook

# 3. (Optional) Run all hooks against the full codebase
pre-commit run --all-files
./gradlew ktlintFormat
```
