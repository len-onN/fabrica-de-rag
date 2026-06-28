package local.fabricarag.core.dto;

public record DocumentPageDto(
    String id,
    Integer filePageNumber,
    String detectedPrintedLabel,
    String effectivePrintedLabel,
    String numberingStyle,
    String pageRole,
    Boolean includeInSearch,
    Boolean includeInNumbering,
    String ocrStatus
) {}
