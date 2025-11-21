package rs.jamie.atlas.arguments;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.Nullable;
import rs.jamie.atlas.AtlasCommandContext;
import rs.jamie.atlas.VelocityAtlasRuntime;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VelocityPlayerArgument implements ArgumentSerializer<Player> {

    private static final ProxyServer server = VelocityAtlasRuntime.proxyServer;

    @Override
    public Player parse(@Nullable String value) {
        if (value == null) return null;
        Optional<Player> player = server.getPlayer(value);
        return player.orElse(null);
    }

    @Override
    public CompletableFuture<Suggestions> suggest(AtlasCommandContext ctx, SuggestionsBuilder builder) {
        server.getAllPlayers().stream()
                .map(Player::getUsername)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

}
