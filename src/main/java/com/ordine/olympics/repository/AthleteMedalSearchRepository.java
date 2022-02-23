package com.ordine.olympics.repository;

import com.ordine.olympics.model.db.AthleteMedalSearch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteMedalSearchRepository extends JpaRepository<AthleteMedalSearch, Long> {
  @Query( "select a.searchedNameSlug from AthleteMedalSearch a where a.searchedNameSlug in :nameSlugs")
  List<String> findByIds(@Param("nameSlugs") List<String> nameSlugs);
}
