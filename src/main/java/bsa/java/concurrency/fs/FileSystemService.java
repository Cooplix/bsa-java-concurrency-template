package bsa.java.concurrency.fs;


import bsa.java.concurrency.image.ImageRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileSystemService implements FileSystem {

    private ImageRepository imageRepository;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Getter
    private static final String PATH = "C:\\bsa\\bsa-java-concurrency-template\\src\\images\\";

    @Override
    public CompletableFuture<String> saveImage(String path, byte[] file) {
        return CompletableFuture.supplyAsync(() -> save(), threadPool);
    }

    private String save() {

        return null;
    }

    @Override
    public void deleteAllImages() {
        File dir = new File(FileSystemService.getPATH());
        File[] files = dir.listFiles();

        for(File file : files) {
            if(!file.delete()){
                System.out.println("File not delete: " + file.getAbsolutePath());
            }
        }
    }

    @Override
    public void deleteImageById(UUID imageId) {
        var image = imageRepository.findOneById(imageId);
        var imagePath = new File(image.getUrl());
        if(!imagePath.delete()) {
            System.out.println("Cant delete file or file not exist");
        }
    }
}
