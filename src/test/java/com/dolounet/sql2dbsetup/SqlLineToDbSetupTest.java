package com.dolounet.sql2dbsetup;


import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlLineToDbSetupTest {

    private SqlLineToDbSetup sqlLineToDbSetup;

    @Test
    public void should_transform_sql_line_with_long_value() {
        initSqlLineToDbSetup("INSERT INTO ROLE (ID) VALUES (1);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"ROLE\").row()\n" +
                ".column(\"ID\", 1).end()");
    }

    @Test
    public void should_transform_sql_line_with_string_value() {
        initSqlLineToDbSetup("INSERT INTO ROLE (NAME) VALUES ('administrator');");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"ROLE\").row()\n" +
                ".column(\"NAME\", \"administrator\").end()");
    }

    @Test
    public void should_transform_sql_line_with_date_value() {
        initSqlLineToDbSetup("INSERT INTO POST (POST_DATE) VALUES (DATE '2014-12-14');");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"POST\").row()\n" +
                ".column(\"POST_DATE\", LocalDate.parse(\"2014-12-14\", DateTimeFormatter.ISO_LOCAL_DATE)).end()");
    }

    @Test
    public void should_transform_sql_line_with_date_time_value() {
        initSqlLineToDbSetup("INSERT INTO POST (POST_DATE) VALUES (TIMESTAMP '2014-12-14 15:42:35');");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"POST\").row()\n" +
                ".column(\"POST_DATE\", LocalDateTime.parse(\"2014-12-14T15:42:35\", DateTimeFormatter.ISO_LOCAL_DATE_TIME)).end()");
    }

    @Test
    public void should_transform_sql_line_with_null() {
        initSqlLineToDbSetup("INSERT INTO POST (POST_DATE) VALUES (NULL);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"POST\").row()\n" +
                ".column(\"POST_DATE\", null).end()");
    }

    @Test
    public void should_transform_sql_line_with_boolean() {
        initSqlLineToDbSetup("INSERT INTO HABS (ADMIN_RIGHTS) VALUES (FALSE);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"HABS\").row()\n" +
                ".column(\"ADMIN_RIGHTS\", false).end()");

        initSqlLineToDbSetup("INSERT INTO HABS (ADMIN_RIGHTS) VALUES (TRUE);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"HABS\").row()\n" +
                ".column(\"ADMIN_RIGHTS\", true).end()");
    }

    @Test
    public void should_transform_sql_line_with_many_columns() throws Exception {
        initSqlLineToDbSetup("INSERT INTO USER (NAME, AGE) VALUES ('Bilbo Baggins', 78);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"USER\").row()\n" +
                ".column(\"NAME\", \"Bilbo Baggins\")\n" +
                ".column(\"AGE\", 78).end()");
    }

    @Test
    public void should_transform_sql_line_with_updates() throws Exception {
        initSqlLineToDbSetup("UPDATE USER SET NAME = 'Bilbo Baggins', AGE = 78;");
        assertThat(sqlLineToDbSetup.transformUpdate()).isEqualTo("sql(\"UPDATE USER SET NAME = 'Bilbo Baggins', AGE = 78;\")");
    }

    @Test
    public void should_transform_sql_line_with_no_blanks() {
        initSqlLineToDbSetup("INSERT INTO POST(POST_DATE) VALUES(NULL);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"POST\").row()\n" +
                ".column(\"POST_DATE\", null).end()");
    }

    @Test
    public void should_transform_sql_line_with_too_much_blanks() {
        initSqlLineToDbSetup("INSERT  INTO    POST   (POST_DATE)  VALUES    (NULL) ;");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"POST\").row()\n" +
                ".column(\"POST_DATE\", null).end()");
    }

    @Test
    public void should_transform_sql_line_with_no_blanks_after_commas() throws Exception {
        initSqlLineToDbSetup("INSERT INTO USER (NAME,AGE) VALUES ('Bilbo Baggins',78);");
        assertThat(sqlLineToDbSetup.transformInsert()).isEqualTo("insertInto(\"USER\").row()\n" +
                ".column(\"NAME\", \"Bilbo Baggins\")\n" +
                ".column(\"AGE\", 78).end()");
    }

    @Test
    @Ignore
    public void should_Name() throws Exception {
        LocalDateTime.parse("2014-12-14T15:42:35", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void initSqlLineToDbSetup(String sqlLine) {
        sqlLineToDbSetup = new SqlLineToDbSetup(sqlLine);
        sqlLineToDbSetup.matchesInsert();
        sqlLineToDbSetup.matchesUpdate();
    }

}
