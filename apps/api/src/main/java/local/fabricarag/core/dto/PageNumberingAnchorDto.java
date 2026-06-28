package local.fabricarag.core.dto;

public record PageNumberingAnchorDto(
    String id,
    Integer filePageNumber,
    String printedLabel,
    String numberingStyle,
    String applyDirection
) {}
