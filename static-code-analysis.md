# 📊 Static Code Analysis Pipeline

This project integrates automated static code analysis using GitHub Actions to ensure high code quality and enforce coding standards.

## 🧰 Tools Included

| Tool             | Description                           |
|------------------|---------------------------------------|
| **Detekt**       | Kotlin static code analysis tool      |
| **Android Lint** | Android-specific linting              |
| **Danger**       | Provides inline PR comments in GitHub |

---

## 🚀 CI/CD Integration

Analysis is triggered on every `push` or `pull_request` to the `main` branch.

### 🔍 Detekt
- Command: `./gradlew detekt`
- Merges with: `mergeDetektSarifReports`
- Report: `build/reports/detekt/merged-detekt-report.sarif`

### 🧹 Android Lint
- Command: `./gradlew lint`
- Merges with: `mergeLintReports`
- Report: `build/reports/lint/merged-lint-report.xml`

### 🧠 Danger
- Parses reports
- Posts inline PR feedback using GitHub API

---

## 🛠️ Local Development

To run checks locally:

### 🔍 Run Detekt
```bash
./gradlew detekt
```
- Output: `build/reports/detekt/`

### 🧪 Run Android Lint
```bash
./gradlew lint
```
- Output: `build/reports/lint/`

### 📦 Merge Reports (multi-module)
```bash
./gradlew mergeDetektSarifReports mergeLintReports
```

---

## 📁 Report Summary

| Tool         | Format | Path                                              |
|--------------|--------|---------------------------------------------------|
| Detekt       | SARIF  | `build/reports/detekt/merged-detekt-report.sarif` |
| Android Lint | XML    | `build/reports/lint/merged-lint-report.xml`       |

---

## 🧐 Run Danger Locally

Install dependencies and execute Danger manually:

```bash
gem install bundler
bundle install
bundle exec danger
```

⚠️ Set `DANGER_GITHUB_API_TOKEN` in your environment or GitHub Secrets for Danger to work.
