package rs.jamie.atlas.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rs.jamie.atlas.AtlasCommandContext;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class OfflinePlayerArgument implements ArgumentSerializer<OfflinePlayer> {

    @Override
    public OfflinePlayer parse(@Nullable String value) {
        if (value == null) return null;
        return Bukkit.getOfflinePlayerIfCached(value);
    }

    @Override
    public CompletableFuture<Suggestions> suggest(AtlasCommandContext ctx, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }


}