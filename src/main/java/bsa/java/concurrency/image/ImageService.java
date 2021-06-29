package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
import bsa.java.concurrency.image.dto.ImageDto;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private final FileSystemService fileSystemService;
    private final ImageRepository imageRepository;
    private final DHasher dHasher;

    @Value("${domain.path}")
    private String domain;

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

    public CompletableFuture<Void> saveFile(List<ImageDto> files) {
        return CompletableFuture.allOf(files
                .parallelStream()
                .map(this::treatmentFile)
                .toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<Void> treatmentFile(ImageDto imageDto) {
        var imgName = fileSystemService.saveImage(imageDto);
        var hash = dHasher.calculateHash(imageDto.getImage());

        return saveFileToFileSystem(imgName, hash);
    }

    private CompletableFuture<Void> saveFileToFileSystem(CompletableFuture<String> imgName, long hash) {
        return imgName
                .thenApply(path -> Image.builder()
                        .hash(hash)
                        .url(domain.concat(path)).build())
                .thenAccept(imageRepository::save);
    }
}
