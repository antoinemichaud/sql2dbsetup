package com.dolounet.sql2dbsetup.run;

import com.dolounet.sql2dbsetup.SqlFileToDbSetup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LaunchConversion {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("The only argument should be the absolute path to your sql file.");
        }

        final String filePath = args[0];
        final Path path = Paths.get(filePath);

        try {
            System.out.println(new SqlFileToDbSetup().transformFile(path.toFile()));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
