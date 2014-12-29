package com.dolounet.sql2dbsetup;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SqlFileToDbSetupTest {
    @Test
    public void should_transform_file_to_dbsetup_script() throws Exception {
        // Given
        final SqlFileToDbSetup sqlFileToDbSetup = new SqlFileToDbSetup();

        // When
        final Path filePath = Paths.get(ClassLoader.getSystemResource("insert_data-test.sql").toURI());
        final String dbSetupScript = sqlFileToDbSetup.transformFile(filePath.toFile());

        // Then
        final URI resultUri = ClassLoader.getSystemResource("result.txt").toURI();
        Assertions.assertThat(dbSetupScript).isEqualTo(Files.toString(new File(resultUri), Charsets.UTF_8));
    }
}
