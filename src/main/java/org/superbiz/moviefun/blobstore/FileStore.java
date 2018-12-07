package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        saveUploadToFile(IOUtils.toByteArray(blob.inputStream), getCoverFile(Long.parseLong(blob.name)));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            Path coverFilePath = getExistingCoverPath(Long.parseLong(name));
            byte[] imageBytes = readAllBytes(coverFilePath);
            String contentType = new Tika().detect(coverFilePath);
            return Optional.of(new Blob(name, new ByteArrayInputStream(imageBytes), contentType));
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void deleteAll() {
        // ...
    }


    private void saveUploadToFile(byte[] uploadedBytes, File targetFile) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedBytes);
        }
    }

    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private File getCoverFile(long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }
}