# Test strategy

Part 1 of 2: the pyramid, the risk matrix and the split between API, UI and manual. Part 2 is not written yet.

The system under test is [automationexercise.com](https://automationexercise.com), a public demo shop that we do not control. Test cases live in Qase (project `PJF`, 285 cases); the number automated is the count of distinct `@QaseId` in the code.

## Test pyramid

```
        /\        UI (Playwright): 28 test methods
       /  \       few, slow, a real browser; only what a user sees and does
      /----\
     / API  \     API (REST Assured): 27 test methods, 40 runs; JSON Schema checks
    /--------\    cheap, fast, check the contract and the data rules
   / checks   \   no unit layer: the framework code has no product logic to unit-test,
  /------------\  only host matching (`AdHostsTest`) and the schemas (`JsonSchemaTest`) run without a browser
```

The pyramid is flatter than the textbook one on purpose. We do not own the application code, so there is no unit layer for the product; the lowest layer we can reach is the site's REST API. The rule is simple: if a rule can be checked through the API, check it there, and keep the UI for what only a browser shows.

UI tests do not build their data through the UI. A user is created through the API (`UserService`) and deleted after the test, which keeps UI tests short and independent. A logged-in state is reused through Playwright `storageState` where a test needs it (`AuthenticatedCartTest`).

## Technical tests

Tests with no Qase case (no `@QaseId`) test the framework, not the product: the ad blocker (`AdBlockerTest`, `AdHostsTest`) and the JSON Schemas (`JsonSchemaTest`). They live in their own suite, `technical/technical-tests.xml`, so that the suites of pages hold only tests that trace to a case. They keep a priority group, so `-Dgroups=high` still runs them.

## Risk matrix

Likelihood of a defect and impact on a user, both rated low, medium or high. The rating decides priority (it matches the Qase priority and the TestNG `groups`) and where the check goes.

| Area | Likelihood | Impact | Priority | Where | State |
|---|---|---|---|---|---|
| Registration and account data | medium | high | high | API for rules, UI for the form | API done; UI registration not automated |
| Login and logout | medium | high | high | UI for the flow, API for wrong credentials | done |
| Checkout, payment, order | medium | high | high | API where it exists, UI for the flow | not automated yet |
| Cart | medium | high | high | UI | basic cases done |
| Product catalog and search | medium | medium | high | API for data, UI for browsing | done |
| Product page and reviews | low | medium | medium | UI | partly done |
| Contact Us | low | low | medium | UI | done |
| Header and navigation | low | medium | medium | UI | done |
| Footer and subscription | low | low | low | UI | not automated yet |
| Ads and consent popups | high | low | not a product risk | they only disturb the tests, see below | blocked in the framework |

Notes on the ratings:

- Money and personal data (checkout, registration) are rated by impact first: a defect there costs the most, even if the code is rarely changed.
- Ads and consent popups are the main source of flaky UI tests on this site, not a product risk. They are blocked at the network level (`AdBlocker`), and two tests check that the blocker works. Without them an empty page would pass "no ads" for nothing.
- The site itself is a risk for the test run: it sometimes answers 503 "heavy load (queue full)". A failure with a 503 body is not a product defect, rerun before reading the code. There is no retry yet.

## What we automate

**In the API layer**

- Rules of the endpoints: required parameters, wrong password, unknown email, duplicate email, wrong HTTP method.
- The contract: JSON Schema of the responses (search, login). The schemas are tested too: `JsonSchemaTest` breaks a valid response one field at a time and checks that the schema rejects it.
- Data for UI tests: user creation and cleanup.
- Why: a check through the API takes about a second and does not depend on locators or on ads. The site answers HTTP 200 with the real code in the body (`responseCode`), so every test reads the body, not the status.

**In the UI**

- Flows a user does in a browser: login, add to cart, remove from cart, contact form with a file, product search, product page against the catalog and the API, header links.
- What only a browser shows: texts, visibility, redirects, state between pages and between sessions.
- Why: a bug in a page or a locator cannot be seen through the API.

**What we do not automate**

| What | Why |
|---|---|
| Payment with real card data | a third-party step on a demo site; a stable check needs a sandbox, and there is none |
| Visual layout and design | needs screenshot baselines that break on every ad or banner change; checked by eye when needed |
| Load and performance | the site is public and shared; a load test from here would be abuse, and it already fails under normal load |
| Ads content | not our product, only blocked |
| Case 287 (dummyjson answers 500 for an invalid token, should be 401) | a defect of a third-party demo API, kept as a manual exploratory case |
| Firefox and WebKit on this machine | Firefox does not start locally (profile error), so the suite runs on Chromium by decision; the `-Dbrowser` switch exists |

## Known gaps

Registration (UI), checkout and order, footer and subscription, the review form and the home page blocks have manual cases in Qase and no automated test yet. The next batches are chosen from this table by risk, highest first.

## Open for part 2

Retry policy for infrastructure errors (503), parallel execution (never run so far), a CI pipeline (postponed on purpose), and how results are reported.