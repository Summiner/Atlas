package rs.jamie.atlas.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import rs.jamie.atlas.AtlasCommandContext;
import rs.jamie.atlas.TextUtil;
import rs.jamie.atlas.annotations.Argument;
import rs.jamie.atlas.annotations.Command;

@Command(name="vexample", description = "Example command for Atlas", permission = "atlas.example", aliases={"vtest"})
public class TestCommand {

    // velocity only command
    @Argument(async=true)
    public static void test(CommandSource stack, Player player, Double amount) {
        stack.sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed &8| &f"+player.getUsername())));
        stack.sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed &8| &f"+amount)));
        stack.sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    // Support for multiple command sources (paper & velocity)
    @Argument(permission="atlas.test")
    public static void abc(AtlasCommandContext stack, Double amount) {
        stack.sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed &8| &f"+amount)));
        stack.sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }


}
