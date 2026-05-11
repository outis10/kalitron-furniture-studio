package com.kalitron.studio.repository;

import com.kalitron.studio.domain.QuoteItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuoteItem entity.
 */
@Repository
public interface QuoteItemRepository extends JpaRepository<QuoteItem, Long> {
    default Optional<QuoteItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuoteItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuoteItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select quoteItem from QuoteItem quoteItem left join fetch quoteItem.cabinet left join fetch quoteItem.hardware left join fetch quoteItem.quote",
        countQuery = "select count(quoteItem) from QuoteItem quoteItem"
    )
    Page<QuoteItem> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select quoteItem from QuoteItem quoteItem left join fetch quoteItem.cabinet left join fetch quoteItem.hardware left join fetch quoteItem.quote"
    )
    List<QuoteItem> findAllWithToOneRelationships();

    @Query(
        "select quoteItem from QuoteItem quoteItem left join fetch quoteItem.cabinet left join fetch quoteItem.hardware left join fetch quoteItem.quote where quoteItem.id =:id"
    )
    Optional<QuoteItem> findOneWithToOneRelationships(@Param("id") Long id);
}
