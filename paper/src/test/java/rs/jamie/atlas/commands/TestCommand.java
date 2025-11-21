package rs.jamie.atlas.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.OfflinePlayer;
import rs.jamie.atlas.AtlasCommandContext;
import rs.jamie.atlas.TextUtil;
import rs.jamie.atlas.annotations.Argument;
import rs.jamie.atlas.annotations.Command;


@Command(name="example", description = "Example command for Atlas", permission = "atlas.example", aliases={"test"})
public class TestCommand {

    // paper only command
    @Argument(async=true)
    public static void test(CommandSourceStack stack, OfflinePlayer player, Double amount) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed &8| &f"+player.getName())));
        stack.getExecutor().sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed&8| &f"+amount)));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    // Support for multiple command sources (paper & velocity)
    @Argument(permission="atlas.test")
    public static void abc(AtlasCommandContext stack, Double amount) {
        stack.sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed &8| &f"+amount)));
        stack.sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }
}
