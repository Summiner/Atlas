package rs.jamie.atlas;


import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import rs.jamie.atlas.VelocityAtlasRuntime;

import java.util.logging.Logger;

@Plugin(id = "atlastest", name = "Atlas Test Plugin", version = "1.1.0",
        url = "https://jamie.rs", description = ":3", authors = {"Summiner"})
public class AtlasTest {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public AtlasTest(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        VelocityAtlasRuntime atlas = new VelocityAtlasRuntime(this, server, logger, "rs.jamie.atlas.commands");
    }

}
