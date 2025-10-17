package de.christophdick.tenga.exception;

public class DocumentSizeLimitException extends ValidationException {

    private static final long MAX_SIZE_BYTES = 1048576; // 1MB

    public DocumentSizeLimitException() {
        super(String.format("Document content exceeds the maximum size limit of %d bytes (1MB)", MAX_SIZE_BYTES));
    }

    public DocumentSizeLimitException(int actualSize) {
        super(String.format("Document content size (%d bytes) exceeds the maximum size limit of %d bytes (1MB)",
            actualSize, MAX_SIZE_BYTES));
    }
}
