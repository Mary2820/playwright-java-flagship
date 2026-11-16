---
name: api-test-gen
description: Take the description of one automationexercise.com API endpoint and generate the POJO, a REST Assured test, a negative test and a JSON Schema, after checking the real response and the "status code in the body" checklist. Use whenever the user gives an endpoint (name, URL, method, a row of the site's API list or a Qase API case) and wants an API test, "сгенерируй API-тест", "тест на REST Assured", "POJO и схема для эндпоинта", "покрой эндпоинт", even if they never say "skill".
---

# API test generation

Turn one endpoint description into reviewed API test code. The site is quirky, so the real response is checked before anything is generated: a spec written from the description alone has already failed once here (it assumed `application/json`, the site sends `text/html`).

Reply in the user's language (usually Russian). The plan is shown and approved before code is written. Nothing is written to Qase.

## Input
An endpoint: method, path, parameters, and what the docs promise (https://automationexercise.com/api_list). If a Qase case exists, fetch it (`qase_get`, project PJF) and use its steps as the expected results; link it with `@QaseId`. If method or path is missing, ask for it.

## Workflow

### 1. Look at the real endpoint first
Send the request yourself with `curl -s -D - -o body.txt` for the success case and for each negative case (missing parameter, unknown value, wrong method). Never infer headers from `curl -I`: HEAD answers differently from GET. Create test users only with throwaway emails and delete them in the same command; never touch real accounts.

Record: HTTP status, `Content-Type`, the body, every field name and type, which fields are empty strings, which are absent.

If the answer is HTML (`503`, "heavy load (queue full)", Cloudflare page), that is the site, not the endpoint: retry once or twice, and say so.

### 2. The checklist "the code is in the body"
Answer each item with evidence from step 1, in a table.

| # | Check | Why |
|---|---|---|
| 1 | HTTP status is 200 for success and for errors | The site sends the real code in `responseCode` inside the JSON, so the status assertion alone proves nothing |
| 2 | Which `responseCode` is returned per case (200, 201, 400, 404, 405) | Each case asserts it explicitly, with a constant |
| 3 | The `message` of each error case, exact text | Asserted in the negative test, text goes to `IConstants` |
| 4 | `Content-Type` of the response | Usually `text/html; charset=utf-8` even for JSON; `ApiSpecs.body()` reads it through `jsonPath()` |
| 5 | Which fields exist only on success (`products`, `user`) and which only on error (`message`) | POJO fields that may be null are not asserted blindly |
| 6 | Real HTTP errors that break the pattern (e.g. `GET /createAccount` gives a true 405 with `{"detail": ...}`) | Such an endpoint needs its own spec, not `ApiSpecs.response()` |
| 7 | State changes: does the call create or delete data | Create through `userService` so cleanup runs; never leave users behind |
| 8 | Parameters sent as query, form body or JSON | `queryParam` for GET, `formParam` for POST/PUT/DELETE of this site |

If an item cannot be checked, say so and do not guess.

### 3. Plan, then wait
Read the code before planning: `api/specs/ApiSpecs`, `models`, `IConstants`, `BaseApiTest`, `UserService`, `api-tests.xml`, the existing API tests. Show:
- **Models**: record names, fields (`@JsonProperty` for snake_case, `@JsonIgnoreProperties(ignoreUnknown = true)`), the wrapper with `responseCode`. Reuse an existing model if the shape is the same; request and response models stay separate.
- **Tests**: class, method names in the style `...Test`, `groups` (priority), `description`, `@QaseId`. One positive test and at least one negative test (the failure case with its `responseCode` and `message`).
- **JSON Schema**: file `src/test/resources/schemas/<name>.json`, what it requires, `additionalProperties`. Mention if `io.rest-assured:json-schema-validator` must be added to `pom.xml` (same version as `rest-assured.version`).
- **Constants**: endpoint, messages, codes, `PAGE_NAME_*` prefix for assertion messages.
- **Suite**: the entries for `api-tests.xml`.
- **Gaps and assumptions** from the checklist.

Stop and wait for an explicit yes. "OK, but change X" means apply X, show only the change, and ask again.

### 4. Generate
Follow the project rules:
- Request through `given().spec(ApiSpecs.request())`, response through `.then().spec(ApiSpecs.response())`, body into the POJO with `ApiSpecs.body(...)`.
- Assertions only through `CustomAssert` with a message that starts with the `PAGE_NAME_*` prefix. Compare `responseCode` and `message` explicitly; compare fields one by one, not a whole object rebuilt from the request.
- Schema validation: `.body(matchesJsonSchemaInClasspath("schemas/<name>.json"))` on the validated response, in the positive test.
- No literals in tests: paths, texts and codes are constants.
- Users come from `userService.create()`; a test that creates and deletes the user itself uses `userService.track(user)`.
- Edit files yourself; subagents are read-only (see `CLAUDE.md`).

### 5. Prove it
- Compile and run the new tests on their own (`./mvnw test -Dtest=Class`).
- A passing test proves nothing yet. Break each one once (wrong expected `responseCode` or message, a field removed from the schema) and confirm it fails with a readable message; then restore.
- If a run fails with an HTML "heavy load" or 503 body, rerun before blaming the code, and say it was the site.
- Then run `api-tests.xml` as a whole.

### 6. Report
Show a table: endpoint case → test method → assertions (code, message, fields, schema). Say "не 100%" if a case from Qase or from the docs has no assertion, and name it. State separately what was verified by running and what only by reading. List changed files and the commands to run. Do not commit unless asked; stage by explicit path.

## Rules that keep this safe
- Real endpoint calls use only throwaway data and clean it up; never log in as a real user.
- No Qase writes: no case edits, no status changes, no runs. `@QaseId` only links in code.
- No code before the plan is approved.
- Never print or store tokens or passwords.
- Do not change existing tests beyond what the plan lists.
