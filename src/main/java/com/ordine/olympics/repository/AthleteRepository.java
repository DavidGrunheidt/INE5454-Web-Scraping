package com.ordine.olympics.repository;

import com.ordine.olympics.model.db.Athlete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
  @Query( "select a.nameSlug from Athlete a where a.nameSlug in :nameSlugs")
  List<String> findByIds(@Param("nameSlugs") List<String> nameSlugs);
}
