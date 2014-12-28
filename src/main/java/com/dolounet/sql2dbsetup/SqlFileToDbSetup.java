package com.dolounet.sql2dbsetup;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SqlFileToDbSetup {
    public String transformFile(String pathName) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(pathName).toURI());
        final List<String> sqlFileLines = Files.readLines(filePath.toFile(), Charsets.UTF_8);

        List<String> sqlOperations = new ArrayList<>();
        for (String sqlFileLine : sqlFileLines) {
            final SqlLineToDbSetup sqlLineToDbSetup = new SqlLineToDbSetup(sqlFileLine);
            if (sqlLineToDbSetup.matchesInsert()) {
                sqlOperations.add(sqlLineToDbSetup.transformInsert() + ".build()");
            } else if (sqlLineToDbSetup.matchesUpdate()) {
                sqlOperations.add(sqlLineToDbSetup.transformUpdate());
            }
        }

        return "sequenceOf(\n" + Joiner.on(",\n").join(sqlOperations) + "\n);";
    }
}
