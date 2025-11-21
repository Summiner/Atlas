package rs.jamie.atlas;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;
import rs.jamie.atlas.annotations.Argument;
import rs.jamie.atlas.annotations.Command;
import rs.jamie.atlas.arguments.ArgumentSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VelocityAtlasRuntime implements AtlasRuntime {

    public static ProxyServer proxyServer;
    private final Object pluginObject;
    private final Logger logger;
    private final ConcurrentHashMap<Class<?>, ArgumentSerializer<?>> serializers = new ConcurrentHashMap<>();

    // Plugin must be an object instance of your plugin class
    public VelocityAtlasRuntime(Object plugin, ProxyServer server, Logger logger, String cmd_pkg) {
        this.pluginObject = plugin;
        if(proxyServer == null) proxyServer = server;
        this.logger = logger;
        registerSerializers("rs.jamie.atlas.arguments");
        registerPackage(cmd_pkg);
    }


    public VelocityAtlasRuntime(Object plugin, ProxyServer server, Logger logger, String cmd_pkg, String arg_pkg) {
        this.pluginObject = plugin;
        if(proxyServer == null) proxyServer = server;
        this.logger = logger;
        registerSerializers("rs.jamie.atlas.arguments");
        registerSerializers(arg_pkg);
        registerPackage(cmd_pkg);
    }

    public void registerCommand(@NotNull Class<?> commandClass) {
        CommandManager commandManager = proxyServer.getCommandManager();
        if(commandClass.isAnnotationPresent(Command.class)) {
            Command cmdAnnotation = commandClass.getAnnotation(Command.class);

            Set<Method> methods = new HashSet<>();
            Collections.addAll(methods, commandClass.getMethods());
            LiteralArgumentBuilder<CommandSource> command = BrigadierCommand.literalArgumentBuilder(cmdAnnotation.name());

            for (Method method : methods) {
                try {
                    registerMethod(method, cmdAnnotation, commandClass, command);
                }  catch(Exception e) {
                    logger.log(Level.SEVERE, "[Atlas] Could not register method " + method.getName(), e);
                }
            }

            CommandMeta commandMeta = commandManager.metaBuilder("test").aliases(cmdAnnotation.aliases())
                    .plugin(pluginObject)
                    .build();

            commandManager.register(commandMeta, new BrigadierCommand(command.build()));
        }
    }

    public void registerMethod(@NotNull Method method, @NotNull Command cmdAnnotation, @NotNull Class<?> commandClass, LiteralArgumentBuilder<CommandSource> command) {
        if(!method.isAnnotationPresent(Argument.class)) return;
        Argument argAnnotation = method.getAnnotation(Argument.class);
        System.out.println(method.getName());
        System.out.println(Arrays.toString(method.getParameters()));

        LiteralArgumentBuilder<CommandSource> methodCommand = BrigadierCommand.literalArgumentBuilder(method.getName());

        Parameter[] parameters = method.getParameters();

        System.out.println("Method Parameters ("+parameters.length+")");

        List<RequiredArgumentBuilder<CommandSource, String>> arguments = new ArrayList<>();

        for(int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            final Parameter finalParam = parameter;
            // DEBUG
            System.out.println("Loading Parameter ("+parameter.getName()+")");
            System.out.println("Parameter Class: "+parameter.getType().getName());
            System.out.println("Serializers Class: "+serializers.get(parameter.getType()).getClass().getName());
            // DEBUG

            RequiredArgumentBuilder<CommandSource, String> argument = BrigadierCommand.requiredArgumentBuilder(parameter.getName(), StringArgumentType.string());
            argument.suggests((ctx, b) -> addSuggestion(ctx, b, finalParam, argAnnotation, cmdAnnotation));
            arguments.add(argument);

            System.out.println("Finished Loading Parameter ("+parameter.getName()+")\n"); // DEBUG
        }

        arguments.getLast().executes(ctx -> {
            if(shouldReject(ctx.getSource(), argAnnotation, cmdAnnotation)) return com.mojang.brigadier.Command.SINGLE_SUCCESS;

            List<Object> args = runExecutor(ctx, parameters);

            if(!cmdAnnotation.nullable() && !argAnnotation.nullable()) {
                for (Object arg : args) {
                    if(arg == null) {
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    }
                }
            }

            if(argAnnotation.async()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        Object instance = commandClass.getDeclaredConstructor().newInstance();
                        method.invoke(instance, args.toArray());
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "[Atlas] Error executing " + method.getName(), e);
                    }
                });
            } else {
                try {
                    Object instance = commandClass.getDeclaredConstructor().newInstance();
                    method.invoke(instance, args.toArray());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "[Atlas] Error executing " + method.getName(), e);
                }
            }

            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        });


        RequiredArgumentBuilder<CommandSource, String> argumentBuilder = null;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<CommandSource, String> current = arguments.get(i);

            if (argumentBuilder != null) {
                current.then(argumentBuilder);
            }

            argumentBuilder = current;
        }

        if (argumentBuilder != null) {
            methodCommand.then(argumentBuilder);
        }

        command.then(methodCommand);
    }

    private List<Object> runExecutor(final CommandContext<CommandSource> ctx, Parameter[] parameters) {
        List<Object> list = new ArrayList<>();
        if(parameters[0].getType() == AtlasCommandContext.class) {
            list.add(new AtlasCommandContext(ctx, ctx.getSource()));
        } else {
            list.add(ctx.getSource());
        }

        for(int p = 1; p < parameters.length; p++) {
            Parameter parameter = parameters[p];
            Class<?> parameter_class = parameter.getType();
            String inputArgument = ctx.getArgument(parameter.getName(), String.class);

            ArgumentSerializer<?> serializer = serializers.get(parameter_class);
            if(parameter_class == String.class) {
                list.add(inputArgument);
                continue;
            }
            if(serializer != null) {
                list.add(serializer.parse(inputArgument));
                continue;
            }
            list.add(null);
        }

        return list;
    }

    private CompletableFuture<Suggestions> addSuggestion(final CommandContext<CommandSource> ctx, final SuggestionsBuilder builder, final Parameter parameter, Argument argAnnotation, Command cmdAnnotation) {
        if(shouldReject(ctx.getSource(), argAnnotation, cmdAnnotation)) return builder.buildFuture();

        Class<?> klazz = parameter.getType();
        ArgumentSerializer<?> serializer = serializers.get(klazz);

        if(klazz == String.class) {
            return builder.buildFuture();
        }

        if(serializer != null) {
            return serializer.suggest(new AtlasCommandContext(ctx, ctx.getSource()), builder);
        }

        builder.suggest("No Serializer Found");
        return builder.buildFuture();
    }

    public void registerPackage(String path) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(path)
                .enableClassInfo()
                .ignoreClassVisibility()
                .scan()) {

            List<Class<?>> classes = scanResult.getAllClasses().loadClasses();

            for (Class<?> klass : classes) {
                registerCommand(klass);
            }
        }
    }

    public boolean shouldReject(@NotNull CommandSource source, Argument argAnnotation, Command cmdAnnotation) {
        if(!source.hasPermission(argAnnotation.permission()) || !source.hasPermission(cmdAnnotation.permission())) {
            source.sendMessage(TextUtil.formatColor(cmdAnnotation.noPermission()));
            return true;
        }
        return false;
    }

    public void registerSerializers(String path) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(path)
                .enableClassInfo()
                .ignoreClassVisibility()
                .scan()) {

            List<Class<ArgumentSerializer>> classes = scanResult.getClassesImplementing(ArgumentSerializer.class).loadClasses(ArgumentSerializer.class);

            for (Class<? extends ArgumentSerializer> clazz : classes) {
                try {
                    ArgumentSerializer<?> serializer = clazz.getDeclaredConstructor().newInstance();
                    Class<?> type = getSerializerType(clazz);
                    if(type == null) continue;
                    serializers.put(type, serializer);
                } catch(Exception e) {
                    logger.log(Level.SEVERE, "[Atlas] Could not load serializer " + clazz.getName(), e);
                }
            }
        }
    }

}
