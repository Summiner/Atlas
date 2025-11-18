package rs.jamie.atlas;

import org.jetbrains.annotations.NotNull;
import rs.jamie.atlas.arguments.ArgumentSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface AtlasRuntime {

    void registerCommand(@NotNull Class<?> commandClass);

    void registerPackage(String path);

    void registerSerializers(String path);

    default Class<?> getSerializerType(Class<?> serializerClass) {
        for (Type type : serializerClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType parameterized && parameterized.getRawType() == ArgumentSerializer.class) {
                Type typeArg = parameterized.getActualTypeArguments()[0];
                if (typeArg instanceof Class<?> c) {
                    return c;
                }
            }
        }
        return null;
    }

}
