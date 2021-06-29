package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
import bsa.java.concurrency.image.dto.ImageDto;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private FileSystemService fileSystemService;
    private ImageRepository imageRepository;
    private DHasher dHasher;

    @Autowired
    public ImageService(FileSystemService fileSystemService, ImageRepository imageRepository, DHasher dHasher) {
        this.fileSystemService = fileSystemService;
        this.imageRepository = imageRepository;
        this.dHasher = dHasher;
    }

    public void deleteById(UUID imageId) {
        fileSystemService.deleteImageById(imageId);
        imageRepository.deleteById(imageId);
    }

    public void purgeImages() {
        imageRepository.deleteAll();
        fileSystemService.deleteAllImages();
    }


    public List<SearchResultDTO> searchMatchesInFile(ImageDto imageDto, double threshold) {

        long hash = dHasher.calculateHash(imageDto.getImage());
        List<SearchResultDTO> resultDTOS = imageRepository.getMatch(hash, threshold);


        return resultDTOS;
    }

    public CompletableFuture<?> saveFile(List<ImageDto> files) {
        return CompletableFuture.allOf(files
                .parallelStream()
                .map(this::saveToFileSystem)
                .toArray(CompletableFuture[]::new));

        //throw new IllegalArgumentException("not implemented");
    }

    private CompletableFuture<Void> saveToFileSystem(ImageDto imageDto) {
        var imgName = fileSystemService.saveImage((MultipartFile) imageDto);
    }
}
