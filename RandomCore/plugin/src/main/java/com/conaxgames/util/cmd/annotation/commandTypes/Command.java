package com.conaxgames.util.cmd.annotation.commandTypes;

import org.bukkit.permissions.PermissionDefault;
import com.conaxgames.rank.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	String[] name();

	Rank rank() default Rank.NORMAL;

	String description() default "";

	String permission() default "";

	PermissionDefault permissionDefault() default PermissionDefault.FALSE;

	boolean requiresOp() default false;

}
