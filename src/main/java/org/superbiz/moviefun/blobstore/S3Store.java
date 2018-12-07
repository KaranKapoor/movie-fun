package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client s3Client;
    private final String photoStorageBucket;
    private Bucket bucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        s3Client.putObject(photoStorageBucket, blob.name, blob.inputStream, objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
            if (s3Object == null) {
                throw new FileNotFoundException("Use Default");
            }
            return Optional.of(new Blob(s3Object.getKey(), s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType()));
        }
        catch (Exception e) {
            return new FileStore().get(name);
        }
    }

    @Override
    public void deleteAll() {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(photoStorageBucket);
        s3Client.deleteObjects(deleteObjectsRequest);
    }
}
