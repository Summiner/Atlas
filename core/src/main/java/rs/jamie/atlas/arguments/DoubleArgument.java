package rs.jamie.atlas.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.Nullable;
import rs.jamie.atlas.AtlasCommandContext;

import java.util.concurrent.CompletableFuture;

public class DoubleArgument implements ArgumentSerializer<Double> {

    @Override
    public Double parse(@Nullable String value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public CompletableFuture<Suggestions> suggest(AtlasCommandContext ctx, SuggestionsBuilder builder) {
        return builder.buildFuture();
    }


}