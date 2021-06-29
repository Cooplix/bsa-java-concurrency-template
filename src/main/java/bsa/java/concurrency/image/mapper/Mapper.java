package bsa.java.concurrency.image.mapper;

import bsa.java.concurrency.image.dto.ImageDto;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class Mapper {
    public static ImageDto imageToDto(MultipartFile image) {
        try {
            return new ImageDto(image.getBytes(), FilenameUtils.getExtension(image.getOriginalFilename()));
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
}
