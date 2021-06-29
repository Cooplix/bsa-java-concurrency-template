package bsa.java.concurrency.fs;


import bsa.java.concurrency.image.ImageRepository;
import bsa.java.concurrency.image.dto.ImageDto;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileSystemService implements FileSystem {

    private ImageRepository imageRepository;

    @Autowired
    public FileSystemService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    private final int threadPoolNThreads = 2;
    //наскільки я зрозумів в даній частині коду я дозволюю працювати максимум 2'ом процесс таскам
    //але якщо їх більше, ніж 2 то вони стають в чергу?
    //не до кінця розумію як працює LinkedBlockingQueue,
    //бо наскільки я зрозумів саме цей класс відповідає за поставку потоків в чергу
    private final ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolNThreads);
    private final Path savePath = Paths.get('.' + File.separator + "images");

    @Getter
    @Value("${path_to_file}")
    private String PATH;

    @Override
    public CompletableFuture<String> saveImage(ImageDto imageDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return saveImagesToFolder(imageDto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
    }

    private String saveImagesToFolder(ImageDto imageDto) throws IOException {
        var pathToImage = savePath.resolve(
                UUID.randomUUID().toString() + '.' + imageDto.getExtension());

        if(!Files.exists(savePath)) {
            Files.createDirectories(savePath);
        }

        try {
            var out = new BufferedOutputStream(Files.newOutputStream(pathToImage));
            out.write(imageDto.getImage());
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return pathToImage.getFileName().toString();
    }


    @Override
    public void deleteAllImages() {
        File dir = new File(getPATH());
        File[] files = dir.listFiles();

        assert files != null;
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
