package bsa.java.concurrency.fs;


import bsa.java.concurrency.image.ImageRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileSystemService implements FileSystem {

    private ImageRepository imageRepository;

    private final int threadPoolNThreads = 2;
    //наскільки я зрозумів в даній частині коду я дозволюю працювати максимум 2'ом процесс таскам
    //але якщо їх більше то вони стають в чергу?
    //не до кінця розумію як працює LinkedBlockingQueue,
    //бо наскільки я зрозумів саме цей класс відповідає за поставку потоків в чергу
    private final ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolNThreads);

    @Getter
    @Value("${path_to_file}")
    private static String PATH;

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
