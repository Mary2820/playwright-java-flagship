---
name: qase-page-coverage
description: Cover a web page with a complete set of manual test cases, get the user's approval, then save them to Qase (project PJF) in a folder (suite) the user names. Use whenever the user sends a page (URL, page object, HTML, screenshot, or just a page name like "Cart" or "Contact Us") and wants test cases, test design, test coverage, "покрыть страницу кейсами", "написать тест-кейсы", or "сохранить кейсы в Qase", even if they never say "Qase" or "skill".
---

# Qase page coverage

Turn one page into a reviewed, complete set of test cases and store the approved ones in Qase. The user is the reviewer: nothing is written to Qase before an explicit approval, because cases in Qase are shared state and the user wants to be able to explain every case in an interview.

Reply in the user's language (usually Russian). Keep case titles and steps in the language of the existing cases in the project; if there are none, ask once.

## Workflow

### 1. Receive the page
The user sends a URL, a page object class, HTML or a screenshot. Gather what you need to see the whole page, not a guess:
- URL given → open it with the browser tooling if available, otherwise fetch it.
- Page object in `src/main/java/.../pages` → read it, plus the matching tests in `src/test` so you know what is already automated.
- Only a name → ask for one of the above.

Do not ask more than you need. If something is ambiguous (login required, locale), pick the sensible default and state it.

### 2. Build the coverage inventory first
"100% coverage" is only meaningful against a list. Before writing cases, enumerate every testable element and behaviour of the page and show the list to the user:
- **Elements**: every link, button, input, select, checkbox, table, image, message area, header and footer part.
- **Behaviours**: each action and its outcomes (success, validation errors, empty state, error state).
- **Data classes**: valid, empty, boundary lengths, invalid format, special characters, very long input, for each input.
- **States**: guest vs authenticated, empty vs filled, page reload / back navigation, session state.
- **Cross-cutting**: page title and URL, navigation to and from the page, responsive/keyboard basics, ad or consent overlays that the site shows.

Then map every inventory item to at least one case. At the end, show a traceability table (inventory item → case numbers) and state any item deliberately left out and why. Coverage is claimed only when no item is unmapped.

### 3. Draft the cases (do not save yet)
One case = one verifiable idea. Fields per case:
- **Title**: starts with the verified behaviour, specific ("Contact form rejects an empty email"), not "Test 1".
- **Preconditions**
- **Steps** with **expected result per step** where the result is observable, so the case can be reused as an automation spec
- **Priority** (high / medium / low), **Type** (functional, smoke, negative, UI…), **Automation** status (Manual, To be automated, or Automated if a test in `src/test` already covers it; name that test)
- **Tags**: the page name plus a kind tag (`smoke`, `negative`, `boundary`…)

Group positive, negative, boundary and UI/navigation cases, and mark a minimal smoke subset. Avoid near-duplicates: if two cases differ only by data, prefer one case with a data table in the step; keep them separate only when the expected result differs.

Present the draft compactly (a table for the overview, full steps for each case) together with the traceability table. Then stop and wait.

### 4. Review loop
The user checks the cases. Apply corrections (add, remove, reword, re-prioritise), show only what changed plus the updated traceability, and ask again. Repeat until the user clearly approves. A comment such as "ok but change X" is not approval of the rest; re-confirm after applying it.

### 5. Ask for the folder name
Only after approval, ask: "В какую папку (suite) в Qase сохранить? Как назвать?" Always ask, never reuse a name from a previous run without confirming. Then:
1. Call `qase_project_context` with code `PJF` and look at the suite tree (if suites were just created in this session, the cache may be stale: check their ids from earlier results instead of assuming the tree is empty).
2. If a suite with that name exists, ask whether to add to it or create a new one (never silently mix or duplicate). If it needs a parent, ask which.
3. Create the suite with `qase_suite_upsert` and keep the returned `id`.

### 6. Save to Qase
- Read `qase_tool_help` for `qase_case_bulk_create` if not yet read this session.
- Create all approved cases in one `qase_case_bulk_create` call (project `PJF`, `suite_id` from step 5, up to 100 per call; split if more). The batch is validated as a whole, so an invalid item means nothing is created: fix and retry.
- Verify with `qase_get` (entity `suite`, the `suite_id`) and compare `cases_count` with the number created. Do not use `qase_project_context` for this: it is cached for 5 minutes and right after a write it shows the old tree, which looks like lost data.
- Report to the user: suite name and id, number of cases created, and the case ids. If anything differs from the approved list, say so.

Rollback is manual (delete in the Qase UI, or `qase_case_delete` / `qase_suite_delete` only if the user asks), so do not delete anything on your own.

## Rules that keep this safe
- Never write to Qase before explicit approval of the case list and the folder name.
- Never print or store the API token; the MCP server already holds it.
- Do not edit or delete existing Qase cases or suites unless asked.
- Do not add `@QaseId` annotations or touch the test code as part of this skill; linking cases to tests is a separate task.
