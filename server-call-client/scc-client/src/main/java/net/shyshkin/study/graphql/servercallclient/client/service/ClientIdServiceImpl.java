package net.shyshkin.study.graphql.servercallclient.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientIdServiceImpl implements ClientIdService {

    public static final String CLIENT_ID = "client-id";

    private final DefaultPropertiesPersister propertiesPersister;

    @Value("${app.client-id.properties-location:client-id.properties}")
    private String clientIdLocation;

    @Value("${app.client-id.value:#{null}}")
    private UUID clientId;

    @PostConstruct
    void init() {
        if (clientId == null) {
            clientId = readClientId();
        }
        if (clientId == null) {
            clientId = createClientId();
        }
    }

    @Override
    public UUID getClientId() {
        return clientId;
    }

    private UUID readClientId() {
        try {
            Properties props = new Properties();
            File f = new File(clientIdLocation);
            InputStream in = new FileInputStream(f);
            propertiesPersister.load(props, in);
            String id = props.getProperty(CLIENT_ID);
            return UUID.fromString(id);
        } catch (FileNotFoundException e) {
            log.debug("{}", e.getMessage());
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private UUID createClientId() {
        try {
            Properties props = new Properties();
            UUID clientId = UUID.randomUUID();
            props.setProperty(CLIENT_ID, clientId.toString());
            File f = new File(clientIdLocation);
            Path clientIdPath = Paths.get(clientIdLocation).toAbsolutePath();
            Files.createDirectories(clientIdPath.getParent());
            OutputStream out = new FileOutputStream(f);
            propertiesPersister.store(props, out, "Client ID");
            return clientId;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
