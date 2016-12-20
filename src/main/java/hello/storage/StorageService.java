package hello.storage;

import hello.FileUploadController;
import hello.parser.CsvToJson;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class StorageService {
    public static final String CSV_FILE = "sigma/data.csv";

    public void store(String path, byte[] bytes) {
        try {
            FileUtils.writeByteArrayToFile(new File(getFileUri(path)), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getFileUri(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Missing storage fils.");
        }
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void store(MultipartFile file) {
        try {
            store(CSV_FILE, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepareJson() {
        try {
            String result = CsvToJson.parse(getFileUri(CSV_FILE));
            store(FileUploadController.JSON_PATH, result.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
