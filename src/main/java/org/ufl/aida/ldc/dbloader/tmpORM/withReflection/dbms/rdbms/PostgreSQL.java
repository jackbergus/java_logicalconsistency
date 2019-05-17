package org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms;

import org.jooq.SQLDialect;
import org.jooq.util.postgres.PostgresDataType;

public class PostgreSQL implements  DBMSInterface {
    @Override
    public String driverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public String connectToDatabaseOnLocalhost(String dbname) {
        return "jdbc:postgresql://localhost/"+dbname;
    }

    @Override
    public String defaultDatabaseName() {
        return "postgres";
    }

    @Override
    public SQLDialect currentDialect() {
        return SQLDialect.POSTGRES;
    }

    @Override
    public Class<?> getAssociatedTypes() {
        return PostgresDataType.class;
    }
}
