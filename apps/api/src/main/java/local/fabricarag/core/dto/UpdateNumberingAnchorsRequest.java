package local.fabricarag.core.dto;

import java.util.List;

public record UpdateNumberingAnchorsRequest(
    List<PageNumberingAnchorDto> anchors
) {}
