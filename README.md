# playwright-java-flagship

UI test automation for [automationexercise.com](https://automationexercise.com) built with Playwright for Java and TestNG.

## Stack

- Java 21
- Playwright for Java 1.63
- TestNG 7.12
- Maven 3.9.9 via Maven Wrapper (`./mvnw`, no local Maven needed)
- REST Assured (the API tests of the catalog, user data and the dummyjson.com token flow; `UserApi` and `ProductApi` use Playwright's request context)
  - Two HTTP clients on purpose: Playwright's request context creates test data for UI tests, REST Assured is what the API tests themselves check
- SLF4J + Logback for logging, AssertJ, Datafaker, Jackson
- Qase TestNG reporter (off by default)

## Project structure

```
src/
├── main/
│   ├── java/io/github/mary2820/
│   │   ├── api/         # UserApi, ProductApi: the site's REST API; AccountRequests, UserForm: REST Assured calls and form fields
│   │   │   └── specs/   # ApiSpecs: REST Assured request and response specs
│   │   ├── assertions/  # CustomAssert: assertions with a message
│   │   ├── components/  # parts shared by pages (HeaderComponent, HeaderItem, ProductCard)
│   │   ├── config/      # Config: reads config.properties, -D overrides
│   │   ├── constants/   # IConstants: texts, paths, priorities
│   │   ├── factories/   # TestUserFactory: random test data
│   │   ├── models/      # TestUser, ApiResponse, Product, Brand, User, login and error models and their responses
│   │   ├── pages/       # Page Objects (BasePage, HomePage, ...)
│   │   ├── services/    # UserService: creates and cleans up users
│   │   └── utils/       # AdBlocker
│   └── resources/
│       └── config.properties
└── test/
    ├── java/io/github/mary2820/
    │   ├── base/        # BaseTest (API clients), BaseUiTest (browser, context, tracing), BaseApiTest (no browser)
    │   └── tests/       # ui/ and api/ tests
    └── resources/
        ├── files/       # files the tests upload
        ├── schemas/     # JSON Schemas of API responses
        └── testngSuites/  # one suite file per page or area
```

Framework code (config, pages, components, assertions, constants) lives in `src/main`; tests and test lifecycle live in `src/test`. Tests contain no locators, only actions on pages and assertions.

Tests are marked with `groups` (`high`, `medium`, `low`, the priority of the Qase case), `description` (the case title) and `@QaseId` / `@QaseIds`. Run one priority with `-Dgroups=high`. Parallel execution is not configured. If you enable it, run classes in parallel, never methods: the state is kept in fields.

## Running tests

Install browsers once:

```bash
./mvnw exec:java -e -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

Run everything (Maven finds every test class itself):

```bash
./mvnw test
```

Run one area from its suite file. The suites live in `src/test/resources/testngSuites`: `api/api-tests.xml`, `ui/<page>-tests.xml` for `home`, `header`, `login`, `products`, `product-detail`, `cart` and `contact-us`, and `technical/technical-tests.xml`. In the IDE run the file itself; with Maven pass its path:

```bash
./mvnw test -Dsurefire.suiteXmlFiles=src/test/resources/testngSuites/ui/cart-tests.xml
```

A new test needs an entry in the suite file of its page so it can be run on its own; `./mvnw test` runs it anyway. A test with no Qase case (no `@QaseId`) is a technical test: it goes to `technical/technical-tests.xml`, not to the suite of a page (today the ad blocker and the JSON Schema checks).

## Configuration

Defaults are in `src/main/resources/config.properties`. Any value can be overridden with `-D`:

| Property   | Default                          | Description                              |
|------------|----------------------------------|------------------------------------------|
| `baseUrl`  | `https://automationexercise.com` | Base URL; tests navigate by path (`/`)   |
| `browser`  | `chromium`                       | `chromium`, `firefox` or `webkit`        |
| `headless` | `true`                           | `false` to see the browser               |
| `timeout`  | `10000`                          | Default timeout for actions, ms          |
| `slowMo`   | `0`                              | Delay between actions, ms                |
| `blockAds` | `true`                           | Abort requests to ad and consent hosts   |
| `logLevel` | `INFO`                           | Project log level, read by `logback-test.xml` and not by `Config`; `DEBUG` shows blocked ad requests |

Examples:

```bash
./mvnw test -Dheadless=false
./mvnw test -Dbrowser=webkit
./mvnw test -Dheadless=false -DslowMo=500
```

## Qase reporting

Tests are linked to Qase cases (project `PJF`) with `@QaseId` / `@QaseIds`. Reporting is off by default, so `./mvnw test` needs no token. The reporter still prints `Starting test run` and `Uploading results` to the log in this mode; nothing is sent. To send results, set the variables for the run (Maven does not read `.env` itself):

| Variable                  | Value                         |
|---------------------------|-------------------------------|
| `QASE_MODE`               | `testops`                     |
| `QASE_TESTOPS_PROJECT`    | `PJF`                         |
| `QASE_TESTOPS_API_TOKEN`  | API token, kept in `.env` (copy `.env.example`) |

```bash
set -a; source .env; set +a
QASE_MODE=testops QASE_TESTOPS_PROJECT=PJF ./mvnw test
```

## Traces on failure

Every test records a Playwright trace. If the test fails, it is saved to `traces/<TestClass>.<testMethod>.zip`; passing tests leave nothing behind.

Open a trace:

```bash
./mvnw exec:java -e -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="show-trace traces/HomePageTest.openSiteTest.zip"
```

or drop the zip onto [trace.playwright.dev](https://trace.playwright.dev).