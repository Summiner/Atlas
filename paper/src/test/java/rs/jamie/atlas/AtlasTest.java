package rs.jamie.atlas;

import org.bukkit.plugin.java.JavaPlugin;
import rs.jamie.atlas.PaperAtlasRuntime;

public class AtlasTest extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperAtlasRuntime atlas = new PaperAtlasRuntime(this, "rs.jamie.atlas.commands");

    }

}
