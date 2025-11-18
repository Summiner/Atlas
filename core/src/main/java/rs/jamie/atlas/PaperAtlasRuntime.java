package rs.jamie.atlas;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import rs.jamie.atlas.annotations.Argument;
import rs.jamie.atlas.annotations.Command;
import rs.jamie.atlas.arguments.ArgumentSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class PaperAtlasRuntime implements Listener, AtlasRuntime {

    private final Plugin plugin;
    private final ConcurrentHashMap<Class<?>, ArgumentSerializer<?>> serializers = new ConcurrentHashMap<>();

    public PaperAtlasRuntime(Plugin plugin, String cmd_pkg) {
        this.plugin = plugin;
        registerSerializers("rs.jamie.atlas.arguments");
        registerPackage(cmd_pkg);
    }

    public PaperAtlasRuntime(Plugin plugin, String cmd_pkg, String arg_pkg) {
        this.plugin = plugin;
        registerSerializers("rs.jamie.atlas.arguments");
        registerSerializers(arg_pkg);
        registerPackage(cmd_pkg);
    }

    public void registerCommand(@NotNull Class<?> commandClass) {
        if(commandClass.isAnnotationPresent(Command.class)) {
            Command cmdAnnotation = commandClass.getAnnotation(Command.class);

            Set<Method> methods = new HashSet<>();
            Collections.addAll(methods, commandClass.getMethods());
            LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(cmdAnnotation.name());

            for (Method method : methods) {
                try {
                    registerMethod(method, cmdAnnotation, commandClass, command);
                }  catch(Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "[Atlas] Could not register method " + method.getName(), e);
                }
            }

            plugin.getLifecycleManager().registerEventHandler(
                    LifecycleEvents.COMMANDS, commands ->
                    commands.registrar().register(command.build())
            );
        }
    }

    public void registerMethod(@NotNull Method method, @NotNull Command cmdAnnotation, @NotNull Class<?> commandClass, LiteralArgumentBuilder<CommandSourceStack> command) {
        if(!method.isAnnotationPresent(Argument.class)) return;
        Argument argAnnotation = method.getAnnotation(Argument.class);
        System.out.println(method.getName());
        System.out.println(Arrays.toString(method.getParameters()));

        LiteralArgumentBuilder<CommandSourceStack> methodCommand = Commands.literal(method.getName());

        Parameter[] parameters = method.getParameters();

        System.out.println("Method Parameters ("+parameters.length+")");

        List<RequiredArgumentBuilder<CommandSourceStack, String>> arguments = new ArrayList<>();

        for(int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            final Parameter finalParam = parameter;
            // DEBUG
            System.out.println("Loading Parameter ("+parameter.getName()+")");
            System.out.println("Parameter Class: "+parameter.getType().getName());
            System.out.println("Serializers Class: "+serializers.get(parameter.getType()).getClass().getName());
            // DEBUG

            RequiredArgumentBuilder<CommandSourceStack, String> argument = Commands.argument(parameter.getName(), StringArgumentType.string());
            argument.suggests((ctx, b) -> addSuggestion(ctx, b, finalParam, argAnnotation, cmdAnnotation));
            arguments.add(argument);

            System.out.println("Finished Loading Parameter ("+parameter.getName()+")\n"); // DEBUG
        }

        arguments.getLast().executes(ctx -> {
            Entity executor = ctx.getSource().getExecutor();
            if(executor == null || shouldReject(executor, argAnnotation, cmdAnnotation)) return com.mojang.brigadier.Command.SINGLE_SUCCESS;

            List<Object> args = runExecutor(ctx, parameters);

            if(!cmdAnnotation.nullable() && !argAnnotation.nullable()) {
                for (Object arg : args) {
                    if(arg == null) {
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    }
                }
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    Object instance = commandClass.getDeclaredConstructor().newInstance();
                    method.invoke(instance, args.toArray());
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "[Atlas] Error executing " + method.getName(), e);
                }
            });

            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        });


        RequiredArgumentBuilder<CommandSourceStack, String> argumentBuilder = null;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<CommandSourceStack, String> current = arguments.get(i);

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

    private List<Object> runExecutor(final CommandContext<CommandSourceStack> ctx, Parameter[] parameters) {
        List<Object> list = new ArrayList<>();
        list.add(ctx.getSource());

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

    private CompletableFuture<Suggestions> addSuggestion(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder, final Parameter parameter, Argument argAnnotation, Command cmdAnnotation) {
        Entity executor = ctx.getSource().getExecutor();
        if(executor == null || shouldReject(executor, argAnnotation, cmdAnnotation)) return builder.buildFuture();

        Class<?> klazz = parameter.getType();
        ArgumentSerializer<?> serializer = serializers.get(klazz);

        if(klazz == String.class) {
            builder.suggest("<TEXT>");
            return builder.buildFuture();
        }

        if(serializer != null) {
            return serializer.suggest(ctx, builder);
        }

        builder.suggest("No_Serializer_Found");
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

    public boolean shouldReject(@NotNull Entity entity, Argument argAnnotation, Command cmdAnnotation) {
        if(!entity.hasPermission(argAnnotation.permission()) || !entity.hasPermission(cmdAnnotation.permission())) {
            entity.sendMessage(TextUtil.formatColor(cmdAnnotation.noPermission()));
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
                    plugin.getLogger().log(Level.SEVERE, "[Atlas] Could not load serializer " + clazz.getName(), e);
                }
            }
        }
    }

}
