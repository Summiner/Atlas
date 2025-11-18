package rs.jamie.atlas.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import rs.jamie.atlas.TextUtil;
import rs.jamie.atlas.annotations.Argument;
import rs.jamie.atlas.annotations.Command;

@Command(name="test", description = "Test command for Atlas", permission = "test.use", aliases={"123", "abc"})
public class TestCommand {

    @Argument(permission="test.msg", async=true)
    public static void start(CommandSourceStack stack, Player player) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed") + " &8| &f"+player.getName()));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    @Argument(permission="test.amt")
    public static void start(CommandSourceStack stack, Player player, Double db) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed") + " &8| &f"+player.getName()));
        stack.getExecutor().sendMessage(TextUtil.formatColor((db == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed") + " &8| &f"+db));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    @Argument(permission="test.amt2", nullable = true)
    public static void start(CommandSourceStack stack, Player player, Double db, Integer in) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed") + " &8| &f"+player.getName()));
        stack.getExecutor().sendMessage(TextUtil.formatColor((db == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed") + " &8| &f"+db));
        stack.getExecutor().sendMessage(TextUtil.formatColor((in == null ? "&bInteger: &cFailed" : "&bInteger: &aPassed") + " &8| &f"+in));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

}
