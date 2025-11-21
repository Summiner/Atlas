# Atlas

> Basic command library for the modern PaperMC Brigadier API


### Installation

[//]: # (Paper Installation)

<details>
<summary>Paper</summary>

#### Gradle (Kotlin)
```kts
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Summiner:Atlas:1.0.0'")
}
```

#### Gradle (Groovy):
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Summiner:Atlas:1.0.0'
}
```

#### Maven:
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.Summiner'</groupId>
  <artifactId>Atlas</artifactId>
  <version>1.0.0</version>
</dependency>
```
</details>



[//]: # (Velocity Installation)

<details>
<summary>Velocity</summary>

#### Gradle (Kotlin)
```kts
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Summiner:Atlas:1.0.0")
}
```

#### Gradle (Groovy):
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Summiner:Atlas:1.0.0'
}
```

#### Maven:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
<groupId>com.github.Summiner'</groupId>
<artifactId>Atlas</artifactId>
<version>1.0.0</version>
</dependency>
```

</details>


### Usage


#### Registering With Paper

```java
@Override
public void onEnable() {
    PaperAtlasRuntime atlas = new PaperAtlasRuntime(this, "your.pkg.commands");
}
```

#### Registering with velocity
```java
@Inject
public ExamplePlugin(ProxyServer server, Logger logger) {
    VelocityAtlasRuntime atlas = new VelocityAtlasRuntime(this, server, logger, "your.pkg.commands");
}
```

\
\
Command Example:
```java
package your.pkg.commands;

@Command(name="example", description = "Example command for Atlas", permission = "example.atlas", aliases={"atlas"})
public class ExampleCommand {
    

    // Paper only command (CommandSourceStack is used by Paper's API)
    @Argument(permission="example.paper")
    public static void paper(CommandSourceStack stack, OfflinePlayer player, Double amount) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed &8| &f"+player.getName())));
        stack.getExecutor().sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed&8| &f"+amount)));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    // Velocity only command (CommandSource is used by Velocity's API)
    @Argument(permission="example.velocity")
    public static void velocity(CommandSource stack, Player player, Double amount) {
        stack.sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed &8| &f"+player.getUsername())));
        stack.sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed &8| &f"+amount)));
        stack.sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }

    // Support for all platforms (AtlasCommandContext is used to generalize all platforms into one provider)
    @Argument(permission="example.all", async=true)
    public static void all(AtlasCommandContext stack, Double amount) {
        stack.sendMessage(TextUtil.formatColor((amount == null ? "&bDouble: &cFailed" : "&bDouble: &aPassed &8| &f"+amount)));
        stack.sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }
    
    
}
```