package main.db;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QueryResult<T> {
    private final List<T> results;
    private final Optional<Long> generated_key;

    private QueryResult(List<T> results, Optional<Long> generated_key) {
        this.results = results;
        this.generated_key = generated_key;
    }

    public static <T> QueryResult<T> ofSelect(List<T> results) {
        return new QueryResult<>(results, Optional.empty());
    }

    public static <T> QueryResult<T> ofInsert(Long generated_key, T inserted) {
        return new QueryResult<>(Collections.singletonList(inserted), Optional.of(generated_key));
    }

    public List<T> getResults() {
        return results;
    }

    public Optional<Long> getGeneratedKey() {
        return generated_key;
    }

    public Optional<T> getSingleResult() {
        if(results.isEmpty())
            return Optional.empty();
        else
            return Optional.of(results.getFirst());
    }
}
