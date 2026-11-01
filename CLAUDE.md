# playwright-java-flagship

UI test automation for automationexercise.com: Java 21, Playwright for Java, TestNG, Maven Wrapper. See `README.md` for the stack, structure and configuration.

## Commands

- `./mvnw test`: run the suite
- `./mvnw test -Dheadless=false`: run with a visible browser
- `./mvnw test -Dbrowser=webkit`: run in another browser

## Code layout

- Framework code (config, pages, utils) lives in `src/main`; tests and test lifecycle live in `src/test`.
- Tests contain no locators, only actions on pages and assertions.

## Git

- Never stage `src/main/resources/config.properties` with `headless=false`. It is a deliberate local change, and the committed value is `headless=true`.
- Stage files by explicit path, never with `git add .`.
- With that local change present, rebase with `git rebase --autostash`.
- Work in feature branches and merge PRs with squash. Commit messages start with a verb in the form `Added: ...`.

## Hooks

`.claude/hooks/subagent-read-only-guard.py` makes subagents read-only: they report proposed changes, and the main agent applies them.