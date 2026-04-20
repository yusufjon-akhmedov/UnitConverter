# Changelog

## 2.0.0

### Architecture

- Replaced the starter single-activity approach with `app`, `data`, `domain`, and `presentation` layers
- Moved conversion logic out of Compose and into use cases plus a dedicated converter engine
- Added manual dependency wiring through an application container

### Product

- Expanded support to 7 conversion categories
- Added searchable unit selection
- Added swap and reset actions
- Added favorites for pinned unit pairs
- Added automatic recent conversion history
- Added theme and last-category persistence
- Added responsive layouts and richer empty/error/result states

### Quality

- Added unit tests for conversion logic and invalid-input handling
- Added ViewModel tests for state transitions and persistence-related behavior
- Added Compose UI test coverage for a core conversion flow
- Added GitHub Actions for lint, build, unit tests, and instrumented tests
- Cleaned repository hygiene and removed tracked IDE metadata
