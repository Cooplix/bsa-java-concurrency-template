package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import bsa.java.concurrency.image.mapper.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/image")
public class ImageController {

    private ImageService imageService;


    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<Void> batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        return imageService
                .saveFile(Arrays
                        .stream(files)
                        .map(Mapper::imageToDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        if(threshold <= 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1");
        }
        return imageService.searchMatchesInFile(Mapper.imageToDto(file), threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        imageService.deleteById(imageId);
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
        imageService.purgeImages();
    }

}
