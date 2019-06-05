package org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model;

import java.lang.annotation.Retention;

/**
 * Associates to each field a data type in SQL
 */
@Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SQLType {
    String type();
}
