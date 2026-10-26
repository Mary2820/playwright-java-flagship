# playwright-java-flagship

UI test automation for [automationexercise.com](https://automationexercise.com) built with Playwright for Java and TestNG.

## Stack

- Java 21
- Playwright for Java 1.63
- TestNG 7.12
- Maven (Surefire 3.6)

## Project structure

```
src/
├── main/
│   ├── java/io/github/mary2820/
│   │   ├── config/      # Config: reads config.properties, -D overrides
│   │   └── pages/       # Page Objects (BasePage, HomePage, ...)
│   └── resources/
│       └── config.properties
└── test/
    ├── java/io/github/mary2820/
    │   ├── base/        # BaseTest: browser, context, tracing lifecycle
    │   └── tests/ui/    # UI tests
    └── resources/
        └── testng.xml
```

Framework code (config, pages) lives in `src/main`; tests and test lifecycle live in `src/test`. Tests contain no locators, only actions on pages and assertions.

## Running tests

Install browsers once:

```bash
mvn exec:java -e -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

Run the suite:

```bash
mvn test
```

## Configuration

Defaults are in `src/main/resources/config.properties`. Any value can be overridden with `-D`:

| Property   | Default                          | Description                              |
|------------|----------------------------------|------------------------------------------|
| `baseUrl`  | `https://automationexercise.com` | Base URL; tests navigate by path (`/`)   |
| `browser`  | `chromium`                       | `chromium`, `firefox` or `webkit`        |
| `headless` | `true`                           | `false` to see the browser               |
| `timeout`  | `10000`                          | Default timeout for actions, ms          |
| `slowMo`   | `0`                              | Delay between actions, ms                |

Examples:

```bash
mvn test -Dheadless=false
mvn test -Dbrowser=webkit
mvn test -Dheadless=false -DslowMo=500
```

## Traces on failure

Every test records a Playwright trace. If the test fails, it is saved to `traces/<TestClass>.<testMethod>.zip`; passing tests leave nothing behind.

Open a trace:

```bash
mvn exec:java -e -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="show-trace traces/HomePageTest.openSiteTest.zip"
```

or drop the zip onto [trace.playwright.dev](https://trace.playwright.dev).