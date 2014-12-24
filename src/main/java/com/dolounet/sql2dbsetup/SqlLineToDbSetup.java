package com.dolounet.sql2dbsetup;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

public class SqlLineToDbSetup {

    private Pattern sqlInsertPattern = Pattern.compile("INSERT *INTO *(\\w*) *\\((.*)\\) *VALUES *\\((.*)\\) *;");
    private Pattern sqlUpdatePattern = Pattern.compile("UPDATE.*;");

    private Matcher sqlInsertLineMatcher;
    private Matcher sqlUpdateLineMatcher;


    public SqlLineToDbSetup(String sqlLine) {
        sqlInsertLineMatcher = sqlInsertPattern.matcher(sqlLine);
        sqlUpdateLineMatcher = sqlUpdatePattern.matcher(sqlLine);
    }

    public boolean matchesInsert() {
        return sqlInsertLineMatcher.find();
    }

    public boolean matchesUpdate() {
        return sqlUpdateLineMatcher.find();
    }

    public String transformInsert() {
        final List<String> columnsNames = newArrayList(Splitter.onPattern(" *, *").split(sqlInsertLineMatcher.group(2)));
        final List<String> values = adaptToJavaSyntax(sqlInsertLineMatcher.group(3));

        final int numberOfColumns = columnsNames.size();
        String columnsNamesAndValuesTemplate = columnsNamesAndValuesTemplate(numberOfColumns);

        String dbSetupQueryTemplate = "insertInto(\"%s\").row()" + columnsNamesAndValuesTemplate + ".end()";

        List<String> valuesToPutInTemplate = new ArrayList<>(numberOfColumns * 2);
        valuesToPutInTemplate.add(sqlInsertLineMatcher.group(1));
        for (int i = 0; i < numberOfColumns; i++) {
            valuesToPutInTemplate.add(columnsNames.get(i));
            valuesToPutInTemplate.add(values.get(i));
        }

        return String.format(dbSetupQueryTemplate, valuesToPutInTemplate.toArray());
    }

    public String transformUpdate() {
        return "sql(\"" + sqlUpdateLineMatcher.group(0) + "\")";
    }

    private static String columnsNamesAndValuesTemplate(int numberOfColumns) {
        final String[] columnsNamesAndValuesTemplates = new String[numberOfColumns];
        Arrays.fill(columnsNamesAndValuesTemplates, "\n.column(\"%s\", %s)");

        return Joiner.on("").join(columnsNamesAndValuesTemplates);
    }

    private List<String> adaptToJavaSyntax(String values) {
        final Iterable<String> splittedValues = Splitter.onPattern(" *, *").split(values);

        List<String> javaAdaptedValues = new ArrayList<>();
        for (String value : splittedValues) {
            if (valuesIsString(value)) {
                javaAdaptedValues.add(toJavaString(value));
            } else if (valueIsBoolean(value)) {
                javaAdaptedValues.add(toJavaBoolean(value));
            } else if (valueIsDate(value)) {
                javaAdaptedValues.add(toJavaDate(value));
            } else if (valueIsDateTime(value)) {
                javaAdaptedValues.add(toJavaDateTime(value));
            } else {
                javaAdaptedValues.add(value.toLowerCase());
            }
        }

        return javaAdaptedValues;
    }

    private static boolean valuesIsString (String value) {
        return value.matches("'.*'");
    }

    private static boolean valueIsBoolean (String value) {
        return value.matches("FALSE|TRUE");
    }

    private static boolean valueIsDate (String value) {
        return value.matches("DATE.*");
    }

    private static boolean valueIsDateTime (String value) {
        return value.matches("TIMESTAMP.*");
    }

    private static String toJavaString (String value) {
        value = "\"" + value.substring(1, value.length() - 1) + "\"";
        return value;
    }

    private static String toJavaBoolean (String value) {
        return value.toLowerCase();
    }

    private static String toJavaDate(String value) {
        Pattern datePattern = Pattern.compile("DATE '(.*)'");
        Matcher dateMatcher = datePattern.matcher(value);
        dateMatcher.find();

        return "LocalDate.parse(\"" + dateMatcher.group(1) + "\", DateTimeFormatter.ISO_LOCAL_DATE)";
    }

    private static String toJavaDateTime(String value) {
        Pattern dateTimePattern = Pattern.compile("TIMESTAMP '(.*) (.*)'");
        Matcher dateTimeMatcher = dateTimePattern.matcher(value);
        dateTimeMatcher.find();

        return "LocalDateTime.parse(\"" + dateTimeMatcher.group(1) + "T" + dateTimeMatcher.group(2) + "\", DateTimeFormatter.ISO_LOCAL_DATE_TIME)";
    }
}
