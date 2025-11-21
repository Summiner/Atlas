package rs.jamie.atlas;


import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class AtlasCommandContext {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public enum SourceType {
        PAPER,
        VELOCITY
    }

    private final CommandContext<CommandSource> velocity_context;
    private final CommandContext<CommandSourceStack> paper_context;

    private final SourceType sourceType;

    public AtlasCommandContext(@NotNull CommandContext<CommandSourceStack> ctx, CommandSourceStack source) {
        this.velocity_context = null;
        this.paper_context = ctx;
        sourceType = SourceType.PAPER;
    }

    public AtlasCommandContext(@NotNull CommandContext<CommandSource> ctx, CommandSource source) {
        this.velocity_context = ctx;
        this.paper_context = null;
        sourceType = SourceType.VELOCITY;
    }



    public SourceType getSourceType() {
        return sourceType;
    }

    public boolean isPaperContext() {
        return paper_context != null;
    }

    public boolean isVelocityContext() {
        return velocity_context != null;
    }

    public CommandContext<CommandSourceStack> getPaperContext() {
        return paper_context;
    }

    public CommandContext<CommandSource> getVelocityContext() {
        return velocity_context;
    }



    public void sendMessage(Component component) {
        sendFormattedMessage(component);
    }

    public void sendMessage(String message) {
        sendFormattedMessage(mm.deserialize(message));
    }

    private void sendFormattedMessage(Component component) {
        switch (sourceType) {
            case PAPER -> {
                paper_context.getSource().getSender().sendMessage(component);
            }
            case VELOCITY -> {
                velocity_context.getSource().sendMessage(component);
            }
            default -> throw new IllegalStateException("Invalid Source Type: " + sourceType);
        }
    }




}
