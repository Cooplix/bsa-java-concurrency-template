package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageService {
    private FileSystemService fileSystemService;
    private ImageRepository imageRepository;

    @Autowired
    public ImageService(FileSystemService fileSystemService, ImageRepository imageRepository) {
        this.fileSystemService = fileSystemService;
        this.imageRepository = imageRepository;
    }

    public void deleteById(UUID imageId) {
        fileSystemService.deleteImageById(imageId);
        imageRepository.deleteById(imageId);
    }
}
