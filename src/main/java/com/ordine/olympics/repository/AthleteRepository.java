package com.ordine.olympics.repository;

import com.ordine.olympics.model.db.Athlete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
  @Query("select a.nameSlug from Athlete a where a.nameSlug in :nameSlugs")
  List<String> findByIds(@Param("nameSlugs") List<String> nameSlugs);

  @Query(
      nativeQuery = true,
      value = "select count(*) * 100.0 / (select count(*) from athlete total where total.country_code = ?1)"
          + "from athlete a where a.country_code = ?1 and "
          + "(a.bronze_medals_count > 0 or a.silver_medals_count > 0 or a.gold_medals_count > 0)")
  double percentageOfAthletesWithMedals(String countryCode);

  @Query(
      nativeQuery = true,
      value = "select count(*) * 100.0 / (select count(*) from athlete total where total.country_code = ?1)"
          + "from athlete a where a.country_code = ?1 and a.gold_medals_count > 0")
  double percentageOfAthletesWithGoldMedals(String countryCode);

  @Query(
      nativeQuery = true,
      value = "select SUM(a.participations_count) * 1.0 / count(*) from athlete a where a.country_code = ?1")
  double athletesAverageOlympicsParticipation(String countryCode);

  @Query(
      nativeQuery = true,
      value = "select count(distinct a.discipline_slug) from athlete a where a.country_code = ?1")
  int totalSportsParticipation(String countryCode);

  @Query(
      nativeQuery = true,
      value = "select count(*)"
          + "from athlete a where a.country_code = ?1 and "
          + "(a.bronze_medals_count > 0 or a.silver_medals_count > 0 or a.gold_medals_count > 0)")
  int totalOfAthletesWithMedals(String countryCode);
}
