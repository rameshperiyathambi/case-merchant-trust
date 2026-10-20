package dev.caseresearch.caselayer.packaging;

import java.util.List;

public record Section(SectionKind kind, List<Claim> claims) {
}
