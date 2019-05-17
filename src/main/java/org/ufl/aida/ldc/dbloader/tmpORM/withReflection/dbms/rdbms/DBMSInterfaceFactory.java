package org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms;

public class DBMSInterfaceFactory {

    /**
     * Given the RDBMS database, it returns the best DBMSInterface towards the relational database
     * @param name
     * @return
     */
    public static DBMSInterface generate(String name) {
        if (name == null)
            return null;
        if (name.equals("PostgreSQL"))
            return new PostgreSQL();
        else
            return null;
    }

}
