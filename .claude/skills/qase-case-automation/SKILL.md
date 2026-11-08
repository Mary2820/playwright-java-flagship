---
name: qase-case-automation
description: Take Qase test cases (project PJF) by ID, judge whether each can be automated and how well it is written, plan the automation (which page methods, constants and data are missing), get the user's approval, then write the Playwright/TestNG test and prove that every step and expected result is covered. Use whenever the user gives case numbers or says "автоматизируй кейс", "автоматизировать кейсы из Qase", "возьми кейс 123", "написать автотест по кейсу", "проверь покрытие кейса", even if they never say "skill".
---

# Qase case automation

Turn manual cases from Qase into automated tests, one reviewed step at a time. The user is the reviewer: the plan is shown and approved before any code is written, because she has to be able to explain every line of the framework in an interview.

Reply in the user's language (usually Russian). Qase is only read, never written.

## Workflow

### 1. Fetch the cases
The user names case IDs. Fetch them in one `qql_search` (`entity = "case" and project = "PJF" and id in (...)`), or `qase_get` for a single id. The result of a wide search can exceed the token limit and gets saved to a file; read it with `jq` rather than guessing. If no IDs are given, ask for them; do not search for candidates yourself.

Per case keep: id, title, preconditions, steps (action + expected result), data, tags, the existing `(automated: methodName)` part of the title, and the suite.

### 2. Assess: can it be automated, and is it well written
Judge each case on two separate axes and show the verdict in a table, with one line of evidence per cell.

**Automatable**
- Every expected result is observable in the browser or API response (text, URL, visibility, count, status code).
- Deterministic: no captcha, e-mail or SMS, payment, random server behaviour, or data that the test cannot create and clean up.
- Preconditions can be set up through the API or storage state, not by hand.
- Does not depend on ads or consent overlays that the site shows unpredictably (the project blocks ad hosts by default).
- Not already covered: look in `src/test` for the method named in the title or for the same check under another name. A duplicate is a finding, not a task.

**Well written**
- One verifiable idea per case.
- Each step has an action and an expected result; no step like "check that everything works".
- Concrete data (exact text, values), not "some valid email".
- Preconditions stated.
- Still true on the live page: open the page (browser tooling if available) and compare. Quote the mismatch. Compare against the rendered page or an element's accessible name, not a text search in raw HTML: markup can split a phrase (`Contact <strong>Us</strong>`), and a missed match looks like a wrong case when the case is right. Before calling a case inaccurate, look at the markup around where the element should be.

Verdict per case: **ready**, **ready with gaps** (list the gaps and the assumption you will make for each), or **not ready**.

### 3. Branch on the verdict
- A case that already has a test (found in step 2) skips planning and writing: go straight to step 7 for it, show the coverage table, and say plainly that there is nothing to automate. If the table shows an open step, offer the fix as a small plan for that gap only. Never write a second test for a case that is covered.
- All cases ready or ready with gaps → go on to step 4. A gap you can fix with a stated assumption is not a reason to stop; show the assumption in the plan.
- Any case not ready → stop for that case and ask the user what to do, naming the options that make sense for it: rewrite the case text (she does it in Qase, this skill does not edit Qase), test only the automatable part, or skip it. Continue with the other cases meanwhile.

### 4. Plan the automation
Read the code before planning, not after: the page objects in `src/main/java/.../pages`, `IConstants`, factories, base test classes, existing tests, `ui-tests.xml` / `testng.xml`. Plan with what really exists.

For each case show a table, one row per step:

| Step | Expected result | Page method | Status | Assertion |
|---|---|---|---|---|

Status is `exists`, `extend` (method exists but needs a parameter or return value) or `missing`. Then list separately:
- **Missing / changed methods**: class, signature, what it does.
- **Missing constants and messages**: where they go (`IConstants`) and the exact text.
- **Test data**: factory, API setup, files; and how it is cleaned up.
- **Test**: class, method name in the project's style (`...Test`), `@QaseId` / `@QaseIds` with the case ids, the entry to add to the suite XML.
- **Assumptions** from step 2 and any case deliberately left out.

Follow the project rules: tests contain no locators, only actions on pages and assertions; framework code lives in `src/main`, tests in `src/test`; locators and waits go into page objects, with a comment where the reason is not obvious.

### 5. Approval gate
Stop after the plan and wait. A comment like "ok, but change X" is not approval of the rest: apply it, show only what changed, and ask again. Write no code before an explicit yes.

### 6. Automate
- Implement exactly the approved plan; if the live page forces a change, tell the user before deviating.
- Edit the files yourself. Subagents here are read-only (see the hook in `CLAUDE.md`); they may research, the main agent applies.
- Compile, then run the new test on its own (`./mvnw test -Dtest=Class#method`).
- A passing test is not proof that it tests anything. For each new test, break the key mechanism once (wrong expected text, skipped step) and confirm it fails; then restore it. Run the final version a few times to catch flakiness.
- Do not run the full suite headed, and do not stage `src/main/resources/config.properties`.

### 7. Check 100% coverage of the case
Build the traceability table from the final code, not from memory:

| Case | Step | Expected result | Test line / assertion | Covered |
|---|---|---|---|---|

A case is covered only if every step is performed and every expected result has its own assertion. If anything is open (a step with no action, a result with no assert, an assertion weaker than the text of the case), say "не 100%", name the exact step, and offer the fix. Do not round up. State separately what was verified by running (green, fails when broken) and what only by reading.

End with the list of changed and added files, the commands to run, and which cases are now covered. Do not commit unless asked; when asked, stage files by explicit path.

## Rules that keep this safe
- Nothing is written to Qase: no case edits, no status changes, no runs. Reading only.
- Never print or store the API token; the MCP server already holds it.
- No code before the plan is approved.
- Do not modify existing tests beyond what the plan lists.
- `@QaseId` / `@QaseIds` links the test to the case in code; it does not touch Qase. Use `@QaseIds({..})` when one test covers several cases.
