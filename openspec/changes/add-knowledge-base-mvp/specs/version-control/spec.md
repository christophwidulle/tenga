# Version Control Specification

## ADDED Requirements

### Requirement: Version History
The system SHALL maintain up to 10 historical versions per document to track changes over time.

#### Scenario: Create initial version
- **WHEN** a user creates a new document
- **THEN** the system creates version 1 of the document
- **AND** the system stores the complete document state including title, content, and tags

#### Scenario: Create new version on update
- **WHEN** a user updates a document
- **THEN** the system creates a new version with an incremented version number
- **AND** the system stores a complete snapshot of the updated document state
- **AND** the system updates the document's last modified timestamp

### Requirement: Maximum Version Limit
The system SHALL retain only the latest 10 versions per document, automatically pruning older versions.

#### Scenario: Pruning old versions
- **WHEN** a document reaches 11 versions
- **THEN** the system automatically deletes the oldest version
- **AND** the system retains versions 2-11 (renumbered as 1-10 or keeping original version numbers)

#### Scenario: Version count enforcement
- **WHEN** a user queries version history
- **THEN** the system returns at most 10 versions

### Requirement: Version Retrieval
The system SHALL allow retrieval of any historical version within the retained history.

#### Scenario: Retrieve specific version
- **WHEN** a user requests a specific version number of a document
- **THEN** the system returns the complete document state at that version
- **AND** the system includes metadata (version number, timestamp, changes summary)

#### Scenario: List version history
- **WHEN** a user requests the version history of a document
- **THEN** the system returns all available versions ordered by version number (newest first)
- **AND** the system includes summary information (version number, timestamp, changed fields)

### Requirement: Version Restore
The system SHALL allow restoring a previous version as the current version.

#### Scenario: Restore historical version
- **WHEN** a user restores a previous version
- **THEN** the system creates a new version (incrementing the version number) with the content from the selected historical version
- **AND** the system does NOT delete or modify the existing version history
- **AND** the system marks the new version with a "restored from version X" indicator

### Requirement: Version Comparison
The system SHALL provide comparison between any two versions of a document.

#### Scenario: Compare consecutive versions
- **WHEN** a user compares version N with version N-1
- **THEN** the system highlights differences in title, content, and tags
- **AND** the system indicates what was added, modified, or removed

#### Scenario: Compare non-consecutive versions
- **WHEN** a user compares version N with version M (where M < N-1)
- **THEN** the system shows cumulative differences between the two versions

### Requirement: Old Versions Not Searchable
The system SHALL NOT include historical versions in search results, only the current version.

#### Scenario: Search excludes old versions
- **WHEN** a user performs a search query
- **THEN** the system returns only current document versions
- **AND** the system does not match content from historical versions

### Requirement: Error Protection
The system SHALL ensure that errors never overwrite or corrupt version history.

#### Scenario: Failed update preserves versions
- **WHEN** an update operation fails (database error, validation error, etc.)
- **THEN** the system does NOT create a new version
- **AND** the system preserves all existing versions unchanged
- **AND** the system returns an appropriate error message

#### Scenario: Transaction rollback on version creation failure
- **WHEN** creating a new version fails partway through
- **THEN** the system rolls back the entire transaction
- **AND** the system leaves the document in its pre-update state

### Requirement: Version Metadata
The system SHALL store metadata with each version including timestamp, version number, and change summary.

#### Scenario: Version metadata capture
- **WHEN** the system creates a new version
- **THEN** the system records the exact timestamp
- **AND** the system stores a summary of what changed (title, content, tags, or combination)
- **AND** the system assigns a monotonically increasing version number

### Requirement: Audit Trail
The system SHALL maintain an audit trail of version changes for transparency and debugging.

#### Scenario: View version audit trail
- **WHEN** a user views version history
- **THEN** the system displays who made changes (when auth is implemented)
- **AND** the system shows when changes were made
- **AND** the system indicates whether the version was a normal update or a restoration
