package bsa.java.concurrency.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    //клас на майбутнє
    private byte[] image;

    private String nameImage;
}
