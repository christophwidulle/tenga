# Web UI Specification

## ADDED Requirements

### Requirement: Document List View
The system SHALL provide a web interface displaying all documents in a list or card layout.

#### Scenario: View document list
- **WHEN** a user opens the web interface
- **THEN** the system displays documents in a list view
- **AND** each item shows title, tags, creation date, and last modified date
- **AND** the system orders documents by last modified (newest first)

#### Scenario: Switch to card view
- **WHEN** a user toggles to card view
- **THEN** the system displays documents as cards with previews
- **AND** the system persists the view preference locally

### Requirement: Document Detail View
The system SHALL provide a detailed view for individual documents with full content and metadata.

#### Scenario: View document details
- **WHEN** a user clicks on a document in the list
- **THEN** the system displays the full document content
- **AND** the system renders Markdown content properly
- **AND** the system shows all associated tags and metadata

#### Scenario: Navigate between documents
- **WHEN** a user is viewing a document
- **THEN** the system provides navigation to previous/next documents
- **AND** the system maintains the current filter context

### Requirement: Tag Filtering
The system SHALL provide tag-based filtering with support for hierarchical tags.

#### Scenario: Filter by tag
- **WHEN** a user clicks on a tag
- **THEN** the system filters documents to show only those with the selected tag
- **AND** the system includes documents with child tags if a parent tag is selected

#### Scenario: Multi-tag filtering
- **WHEN** a user selects multiple tags
- **THEN** the system shows documents matching ALL selected tags (AND logic)
- **AND** the system provides an option to switch to OR logic

#### Scenario: Clear filters
- **WHEN** a user clears tag filters
- **THEN** the system shows all documents again

### Requirement: Search Interface
The system SHALL provide a search interface for full-text and semantic search.

#### Scenario: Perform search
- **WHEN** a user enters a search query
- **THEN** the system displays matching documents
- **AND** the system highlights matching terms in titles and snippets

#### Scenario: Search with filters
- **WHEN** a user combines search with tag filters
- **THEN** the system applies both search and filter criteria

#### Scenario: Toggle semantic search
- **WHEN** a user enables semantic search mode
- **THEN** the system performs AI-powered similarity search
- **AND** the system displays similarity scores

### Requirement: Version History View
The system SHALL provide a version history interface for viewing and comparing document versions.

#### Scenario: View version history
- **WHEN** a user opens version history for a document
- **THEN** the system displays up to 10 historical versions
- **AND** each version shows timestamp and change summary

#### Scenario: View specific version
- **WHEN** a user clicks on a historical version
- **THEN** the system displays the complete version content
- **AND** the system clearly indicates this is a historical version

#### Scenario: Compare versions
- **WHEN** a user selects two versions to compare
- **THEN** the system displays a diff view highlighting changes
- **AND** the system shows additions, deletions, and modifications

#### Scenario: Restore version
- **WHEN** a user restores a historical version
- **THEN** the system prompts for confirmation
- **AND** the system creates a new current version from the historical one
- **AND** the system updates the document list

### Requirement: API-Key Management
The system SHALL provide an interface for entering and managing the API-key.

#### Scenario: Enter API-key
- **WHEN** a user first accesses the web interface
- **THEN** the system prompts for an API-key
- **AND** the system validates the key with the backend
- **AND** the system stores the key locally (LocalStorage or secure cookie)

#### Scenario: Invalid API-key
- **WHEN** a user enters an invalid API-key
- **THEN** the system displays an error message
- **AND** the system prompts to re-enter the key

#### Scenario: Persistent session
- **WHEN** a user returns to the web interface
- **THEN** the system uses the stored API-key
- **AND** the system does not prompt for re-authentication

#### Scenario: Clear API-key
- **WHEN** a user logs out
- **THEN** the system clears the stored API-key
- **AND** the system prompts for re-authentication on next access

### Requirement: Responsive Design
The system SHALL provide a responsive web interface that works on desktop and mobile devices.

#### Scenario: Desktop view
- **WHEN** a user accesses the interface on a desktop
- **THEN** the system displays a multi-column layout with sidebar navigation

#### Scenario: Mobile view
- **WHEN** a user accesses the interface on a mobile device
- **THEN** the system adapts to a single-column layout
- **AND** the system provides touch-friendly navigation

### Requirement: Tag Hierarchy Visualization
The system SHALL visualize tag hierarchies to help users understand relationships.

#### Scenario: Display tag tree
- **WHEN** a user views the tag filter panel
- **THEN** the system displays tags in a hierarchical tree structure
- **AND** the system shows parent-child relationships visually
- **AND** the system indicates document count per tag

#### Scenario: Expand/collapse tag hierarchy
- **WHEN** a user clicks on a parent tag
- **THEN** the system expands or collapses child tags
- **AND** the system maintains expansion state during the session

### Requirement: Document Editing
The system SHALL provide in-browser editing capabilities for documents.

#### Scenario: Edit document
- **WHEN** a user clicks edit on a document
- **THEN** the system displays an editor with current content
- **AND** the system provides Markdown syntax highlighting
- **AND** the system shows a preview pane

#### Scenario: Save edited document
- **WHEN** a user saves changes
- **THEN** the system creates a new version via API
- **AND** the system updates the displayed content
- **AND** the system shows a success confirmation

#### Scenario: Cancel editing
- **WHEN** a user cancels editing
- **THEN** the system discards changes
- **AND** the system returns to the detail view

### Requirement: Create New Document
The system SHALL provide an interface for creating new documents through the web UI.

#### Scenario: Create new document
- **WHEN** a user clicks "New Document"
- **THEN** the system displays an editor for title, content, and tags
- **AND** the system suggests tags based on content as user types

#### Scenario: Save new document
- **WHEN** a user saves a new document
- **THEN** the system creates the document via API
- **AND** the system displays the newly created document

### Requirement: Delete Document
The system SHALL provide an interface for deleting documents.

#### Scenario: Delete document
- **WHEN** a user clicks delete on a document
- **THEN** the system prompts for confirmation
- **AND** the system soft-deletes the document via API
- **AND** the system removes it from the list view

### Requirement: Loading States
The system SHALL provide clear loading indicators during asynchronous operations.

#### Scenario: Loading documents
- **WHEN** the system fetches documents from the API
- **THEN** the system displays a loading indicator
- **AND** the system prevents duplicate requests

#### Scenario: Search in progress
- **WHEN** a search is being performed
- **THEN** the system shows a loading state in the search results area

### Requirement: Error Handling
The system SHALL display user-friendly error messages when operations fail.

#### Scenario: API error
- **WHEN** an API request fails
- **THEN** the system displays an error message
- **AND** the system provides retry or recovery options

#### Scenario: Network error
- **WHEN** the network connection is lost
- **THEN** the system displays a connection error
- **AND** the system retries automatically when connection is restored
