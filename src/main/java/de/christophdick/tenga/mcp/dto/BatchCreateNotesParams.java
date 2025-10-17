package de.christophdick.tenga.mcp.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BatchCreateNotesParams {

    @NotEmpty(message = "At least one note is required")
    private List<CreateNoteParams> notes;

    public BatchCreateNotesParams() {
    }

    public List<CreateNoteParams> getNotes() {
        return notes;
    }

    public void setNotes(List<CreateNoteParams> notes) {
        this.notes = notes;
    }
}
