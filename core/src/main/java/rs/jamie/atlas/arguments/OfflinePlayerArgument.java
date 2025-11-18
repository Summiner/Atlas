package rs.jamie.atlas.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class OfflinePlayerArgument implements ArgumentSerializer<OfflinePlayer> {

    @Override
    public OfflinePlayer parse(@Nullable String value) {
        if (value == null) return null;
        return Bukkit.getOfflinePlayerIfCached(value);
    }

    @Override
    public CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }



}