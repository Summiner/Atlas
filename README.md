# Atlas

> Basic command library for the modern PaperMC Brigadier API


### Installation
<details>
<summary>Gradle (Kotlin)</summary>

```kts
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Summiner:Atlas:1.0.0")
}
```
</details>

<details>
<summary>Gradle (Groovy)</summary>

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Summiner:Atlas:1.0.0'
}
```
</details>

<details>
<summary>Maven</summary>

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.Summiner</groupId>
  <artifactId>Atlas</artifactId>
  <version>1.1.1</version>
</dependency>
```
</details>


### Usage

<details>
<summary>Java Example</summary>

Registering:
```java
@Override
public void onEnable() {
    PaperAtlasRuntime atlas = new PaperAtlasRuntime(this, "rs.jamie.atlas.commands");

}
```

Example Command:
```java
@Command(name="example", description = "Example command for Atlas", permission = "atlas.example", aliases={"test"})
public class TestCommand {
    
    @Argument(async=true)
    public static void test(CommandSourceStack stack, OfflinePlayer player) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed") + " &8| &f"+player.getName()));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }
    
    @Argument(permission="atlas.test")
    public static void abc(CommandSourceStack stack, Player player) {
        stack.getExecutor().sendMessage(TextUtil.formatColor((player == null ? "&bPlayer: &cFailed" : "&bPlayer: &aPassed") + " &8| &f"+player.getName()));
        stack.getExecutor().sendMessage(TextUtil.formatColor("&bCommand: &aPassed"));
    }
}
```