package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
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

    private byte[] getImgBytes(MultipartFile file) throws FileNotFoundException {
        //на данний момент, я не придумав нічого кращого
        //якщо можна буду вдячний за якись розвязок
        try {
            return file.getBytes();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        throw new FileNotFoundException();
    }

    public List<SearchResultDTO> searchMatchesInFile(MultipartFile file, double threshold) throws FileNotFoundException {

        long hash = dHasher.calculateHash(getImgBytes(file));
        List<SearchResultDTO> resultDTOS = imageRepository.getMatch(hash, threshold);


        return resultDTOS;
        //throw new IllegalArgumentException("not implemented");
    }

    public CompletableFuture<?> saveFile(MultipartFile[] files) {
        throw new IllegalArgumentException("not implemented");
    }
}
