# playwright-java-flagship

UI test automation for automationexercise.com: Java 21, Playwright for Java, TestNG, Maven Wrapper. See `README.md` for the stack, structure and configuration.

## Commands

- `./mvnw test`: run the suite
- `./mvnw test -Dheadless=false`: run with a visible browser
- `./mvnw test -Dbrowser=webkit`: run in another browser

## Code layout

- Framework code (config, pages, utils) lives in `src/main`; tests and test lifecycle live in `src/test`.
- Tests contain no locators, only actions on pages and assertions.

## Where things live

- `src/main/.../pages` and `components`: Page Objects, locators are fields, `BasePage.navigate` for paths. `api`, `services`, `factories`, `models`: the site's REST API and test data. `constants/IConstants`: texts, paths, priorities. `assertions/CustomAssert`: assertions with a message.
- `src/test/.../base`: browser, context and tracing lifecycle. `tests/ui`, `tests/api`: the tests.
- `src/test/resources/testngSuites`: one suite file per page or area. A new test is added to the suite of its page.

## Test rules

- TestNG: `groups` is the Qase priority (`high`, `medium`, `low`), `description` is the case title, `@QaseId` links the case. Classes may run in parallel, methods may not (state is in fields).
- No `Thread.sleep` or `waitForTimeout`: rely on Playwright auto-waiting and web-first assertions (`CustomAssert.assertThat(locator)`).
- Locators: prefer `getByRole` (with a name); fall back to CSS or id only when the element has no role or accessible name.
- Texts, paths and test data are constants or factories, never literals in a test.
- Before a PR: run the affected suite file; the PR description says what was and was not run.

## Git

- Never stage `src/main/resources/config.properties` with `headless=false`. It is a deliberate local change, and the committed value is `headless=true`.
- Stage files by explicit path, never with `git add .`.
- With that local change present, rebase with `git rebase --autostash`.
- Work in feature branches and merge PRs with squash. Commit messages start with a verb in the form `Added: ...`.

## Hooks

`.claude/hooks/subagent-read-only-guard.py` makes subagents read-only: they report proposed changes, and the main agent applies them.