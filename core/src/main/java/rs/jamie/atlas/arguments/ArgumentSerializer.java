package rs.jamie.atlas.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import rs.jamie.atlas.AtlasCommandContext;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public interface ArgumentSerializer<T> {

    T parse(@Nullable String value);

    default CompletableFuture<Suggestions> suggest(AtlasCommandContext ctx, SuggestionsBuilder builder) {
        return null;
    }


}
