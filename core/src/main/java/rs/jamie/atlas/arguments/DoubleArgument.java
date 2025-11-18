package rs.jamie.atlas.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

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
    public CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        builder.suggest("Double");
        return builder.buildFuture();
    }
}