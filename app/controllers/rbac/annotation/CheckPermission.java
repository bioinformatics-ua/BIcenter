package controllers.rbac.annotation;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luis on 21/10/14.
 */
@With(CheckPermissionAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission
{
    //By default needs all Permissions
    String category() default "ALL";
    String[] needs() default {"VIEW","EDIT"};
}
