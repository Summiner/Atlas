package rs.jamie.atlas.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public interface ArgumentSerializer<T> {

    T parse(@Nullable String value);

    CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder);

}
