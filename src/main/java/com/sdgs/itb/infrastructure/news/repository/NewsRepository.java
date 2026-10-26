package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    @Query("""
        SELECT n FROM News n
        WHERE LOWER(n.sourceUrl)
        LIKE LOWER(CONCAT(:prefix, '%'))
    """)
    List<News> findBySourceUrlStartingWith(@Param("prefix") String prefix);

    @Query("SELECT a FROM News a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<News> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM News a WHERE LOWER(a.title) = LOWER(:title) ORDER BY a.id ASC")
    List<News> findByTitleIgnoreCase(@Param("title") String title);

    @Query("""
        SELECT n FROM News n
        WHERE n.scholarYear IS NULL
    """)
    List<News> findAllCommunityServiceNews();

    // Direct Patch Query for Paper Metadata
    @Modifying
    @Transactional
    @Query("""
        UPDATE News n
        SET n.cite = COALESCE(:cite, n.cite),
            n.sjr = COALESCE(:sjr, n.sjr),
            n.source = COALESCE(:source, n.source)
        WHERE LOWER(n.sourceUrl) = LOWER(:sourceUrl)
    """)
    int updatePaperMetadata(
            @Param("sourceUrl") String sourceUrl,
            @Param("cite") Integer cite,
            @Param("sjr") String sjr,
            @Param("source") String source
    );
}
