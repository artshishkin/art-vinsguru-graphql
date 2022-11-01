package net.shyshkin.study.graphql.movieapp.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class VersionUtil {

    private static Map<String, String> versions;

    private static final String ENV_FILE_PATH = "../qraphql-oauth2/.env";

    public static String getVersion(String versionKey) {
        if (versions == null) {
            versions = getEnvVariables();
        }
        return versions.get(versionKey);
    }

    private static Map<String, String> getEnvVariables() {
        Properties properties = new Properties();
        try (Reader reader = new FileReader(ENV_FILE_PATH)) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> e.getValue().toString()));
    }

}
