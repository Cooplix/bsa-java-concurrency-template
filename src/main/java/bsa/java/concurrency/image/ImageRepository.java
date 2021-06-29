package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface ImageRepository extends JpaRepository<Image, UUID> {
    Image findOneById(UUID imageId);


    @Query(nativeQuery = true,
                value = " SELECT cast(id as varchar(255)) as imageId, " +
                        " hemmingmatchpercent(hash, :hash) * 100 as percent, " +
                        " url as imageUrl " +
                        " FROM images " +
                        " WHERE hemmingmatchpercent(:hash, hash) >= :threshold " +
                        " ORDER BY percent DESC ")
    List<SearchResultDTO> getMatch(@Param("hash") long hash, @Param("threshold") double threshold);

}
