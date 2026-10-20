package dev.caseresearch.caselayer.retrieval;

import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.Scored;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * In-house BM25 over the artifact catalog; produces topical relevance R(a, q) only (paper Fig. 2, "retrieve").
 * It deliberately knows nothing about deployments.
 */
@Component
public class Bm25Retriever {

    private static final double K1 = 1.5;
    private static final double B = 0.75;

    private final List<Artifact> docs;
    private final List<Map<String, Integer>> termFreqs = new ArrayList<>();
    private final Map<String, Integer> docFreq = new HashMap<>();
    private final double avgLen;
    private final int[] lengths;

    public Bm25Retriever(ArtifactCatalog catalog) {
        this.docs = catalog.all();
        this.lengths = new int[docs.size()];
        int total = 0;
        for (int i = 0; i < docs.size(); i++) {
            Artifact a = docs.get(i);
            List<String> tokens = tokenize(a.title() + " " + a.summary() + " " + a.text());
            lengths[i] = tokens.size();
            total += tokens.size();
            Map<String, Integer> tf = new HashMap<>();
            tokens.forEach(t -> tf.merge(t, 1, Integer::sum));
            tf.keySet().forEach(t -> docFreq.merge(t, 1, Integer::sum));
            termFreqs.add(tf);
        }
        this.avgLen = docs.isEmpty() ? 1 : (double) total / docs.size();
    }

    static List<String> tokenize(String text) {
        String split = text.replaceAll("([a-z0-9])([A-Z])", "$1 $2").toLowerCase();
        return Arrays.stream(split.split("[^a-z0-9]+")).filter(s -> s.length() > 1).toList();
    }

    public List<Scored> topK(String query, int k) {
        List<String> q = tokenize(query);
        List<Scored> scored = new ArrayList<>();
        for (int i = 0; i < docs.size(); i++) {
            double score = 0;
            for (String term : q) {
                Integer f = termFreqs.get(i).get(term);
                if (f == null) {
                    continue;
                }
                int n = docFreq.get(term);
                double idf = Math.log(1 + (docs.size() - n + 0.5) / (n + 0.5));
                score += idf * (f * (K1 + 1)) / (f + K1 * (1 - B + B * lengths[i] / avgLen));
            }
            if (score > 0) {
                scored.add(new Scored(docs.get(i), score));
            }
        }
        scored.sort(Comparator.comparingDouble(Scored::score).reversed().thenComparing(s -> s.artifact().id()));
        return scored.size() > k ? scored.subList(0, k) : scored;
    }
}
