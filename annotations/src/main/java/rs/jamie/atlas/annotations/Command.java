package rs.jamie.atlas.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String name();
    String description() default "";
    String permission() default "";
    String noPermission() default "&cYou do not have permission to use this command!";
    String[] aliases() default {};
    boolean nullable() default false;

}
