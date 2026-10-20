package dev.caseresearch.caselayer.packaging;

import dev.caseresearch.caselayer.model.TaskRequest;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Adds claims to sections while enforcing the shared token budget B. */
public final class Packer {

    private static final int HEADER_TOKENS = 60;

    private final int budget;
    private int used = HEADER_TOKENS;
    private int dropped;
    private final Map<SectionKind, List<Claim>> sections = new EnumMap<>(SectionKind.class);

    public Packer(int budget) {
        this.budget = budget;
    }

    public static int tokens(String s) {
        return (s.length() + 3) / 4;
    }

    public boolean add(SectionKind kind, Claim c) {
        int t = tokens(c.render());
        if (used + t > budget) {
            dropped++;
            return false;
        }
        used += t;
        sections.computeIfAbsent(kind, k -> new ArrayList<>()).add(c);
        return true;
    }

    public ContextPackage build(TaskRequest req, List<String> notes, SectionKind... order) {
        List<Section> out = new ArrayList<>();
        for (SectionKind k : order) {
            if (sections.containsKey(k)) {
                out.add(new Section(k, sections.get(k)));
            }
        }
        return new ContextPackage(req.condition(), req.task(), req.deployment(), req.asOf(), budget, used, dropped,
                out, notes);
    }
}
