package com.ordine.olympics.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordine.olympics.commons.OlympicsApiRouter;
import com.ordine.olympics.model.db.Athlete;
import com.ordine.olympics.model.db.AthleteMedalSearch;
import com.ordine.olympics.model.olympics.dto.AthleteDTO;
import com.ordine.olympics.model.olympics.dto.AthleteMedalsDTO;
import com.ordine.olympics.model.olympics.dto.AthletesDTO;
import com.ordine.olympics.model.response.Answers;
import com.ordine.olympics.repository.AthleteMedalSearchRepository;
import com.ordine.olympics.repository.AthleteRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthletesService {

  @Autowired
  private AthleteRepository athleteRepository;

  @Autowired
  private AthleteMedalSearchRepository athleteMedalSearchRepository;

  private String buildAthletesApiQueryUrl(String query, int querySize, int skip) {
    final String apiQueryPath = String.format(OlympicsApiRouter.searchAthletes, query, querySize,
        skip);
    return String.format("%s%s", OlympicsApiRouter.baseUrl, apiQueryPath);
  }

  @SuppressWarnings("unchecked")
  public AthletesDTO scrapeAthletes() {
    final Random rand = new Random();
    final RestTemplate restTemplate = new RestTemplate();
    final ObjectMapper mapper = new ObjectMapper();

    final int querySize = 1000;
    final int maxSkip = 100000; // Limitation of the API. We can only skip until 10^6

    final boolean offestIsNegative = rand.nextBoolean();
    final int initialskip = rand.nextInt((maxSkip / querySize) + 1) * querySize;
    final int skipOffest = offestIsNegative ? -rand.nextInt(100) : rand.nextInt(100);
    final int skip = initialskip + skipOffest < maxSkip ? initialskip + skipOffest : initialskip;

    assert (skip < maxSkip);

    final List<String> desiredCountryCodes = Arrays.asList("BRA", "USA");
    final String url = buildAthletesApiQueryUrl("", querySize, skip);
    log.info(String.format("GET - %s", url));

    final String resultRaw = restTemplate.getForObject(url, String.class);
    try {
      final Map<String, Object> resultMap = mapper.readValue(resultRaw, Map.class);
      final List<Map<String, Object>> modules = mapper.convertValue(resultMap.get("modules"),
          List.class);

      final AthletesDTO athletesRaw = mapper.convertValue(modules.get(0), AthletesDTO.class);
      final List<AthleteDTO> athletesFromUsAndBr = athletesRaw.athletes.parallelStream()
          .filter(athlete -> desiredCountryCodes.contains(athlete.countryCode)
              && athlete.discipline != null && !athlete.discipline.isBlank())
          .collect(Collectors.toList());

      final List<String> nameSlugs = athletesFromUsAndBr.parallelStream()
          .map(athleteDTO -> athleteDTO.nameSlug)
          .collect(Collectors.toList());

      final List<String> alreadySavedAthletes = athleteRepository.findByIds(nameSlugs);

      final List<AthleteDTO> desiredAthletes = athletesFromUsAndBr.parallelStream()
          .filter(athleteDTO -> !alreadySavedAthletes.contains(athleteDTO.nameSlug))
          .collect(Collectors.toList());

      final List<Athlete> athletesToSave = desiredAthletes.parallelStream()
          .map(AthleteDTO::toAthlete)
          .collect(Collectors.toList());

      athleteRepository.saveAll(athletesToSave);

      return AthletesDTO.builder()
          .athletesScraped(athletesFromUsAndBr.size())
          .athletesAlreadySaved(alreadySavedAthletes.size())
          .newSavedAthletes(athletesFromUsAndBr.size() - alreadySavedAthletes.size())
          .athletes(desiredAthletes)
          .build();
    } catch (JsonProcessingException e) {
      log.error(String.valueOf(e));
      return AthletesDTO.builder().build();
    }
  }

  private List<Athlete> getAthletesToScrapeMedals() {
    final Random rand = new Random();

    final int querySize = 25;
    final int maxPage = (int) ((athleteRepository.count() - querySize) / querySize);
    final int maxAtempts = 10;

    for (int index = 0; index < maxAtempts; index++) {
      final int page = rand.nextInt(maxPage + 1);
      assert (page < maxPage);

      final Page<Athlete> athletesToScrapeMedals = athleteRepository.findAll(PageRequest.of(page, querySize));
      final List<Athlete> athletes = athletesToScrapeMedals.getContent();

      if (athleteMedalSearchRepository.findByIds(List.of(athletes.get(0).nameSlug)).isEmpty()) {
        log.info(String.format("Found page %s not scraped yet", page));
        return athletes;
      }
    }

    log.info("Max attempts reached. Returning empty list");
    return List.of();
  }

  public List<AthleteMedalsDTO> scrapeAthletesMedals() {
    final List<Athlete> athletesToScrapeMedals = getAthletesToScrapeMedals();
    if (athletesToScrapeMedals.isEmpty()) {
      return List.of();
    }

    final List<Athlete> updatedAthletes = athletesToScrapeMedals.parallelStream()
        .peek(athlete -> {
          try {
            final Document doc = Jsoup.connect(String.format("%s%s", OlympicsApiRouter.baseUrl, athlete.url)).get();
            final Element goldCountElement = doc.getElementsByClass("medal-count -gold").first();
            final Element silverCountElement = doc.getElementsByClass("medal-count -silver").first();
            final Element bronzeCountElement = doc.getElementsByClass("medal-count -bronze").first();
            final Optional<Element> participationsCountElement = doc.getElementsByClass("detail__item").stream()
                .filter(element -> element.child(0).text().equals("Participações")).findFirst();

            athlete.goldMedalsCount = goldCountElement != null ? Integer.parseInt(goldCountElement.child(0).text()) : 0;
            athlete.silverMedalsCount = silverCountElement != null ? Integer.parseInt(silverCountElement.child(0).text()) : 0;
            athlete.bronzeMedalsCount = bronzeCountElement != null ? Integer.parseInt(bronzeCountElement.child(0).text()) : 0;
            athlete.participationsCount = participationsCountElement.map(element -> Integer.parseInt(element.child(1).text())).orElse(0);

            Thread.sleep(100);
          } catch (Exception ex) {
            log.error("Error counting athlete %s info {}", athlete.url, ex);
          }
        })
        .collect(Collectors.toList());

    final List<AthleteMedalSearch> athleteMedalSearches = updatedAthletes.parallelStream()
        .map(athlete -> AthleteMedalSearch.builder().searchedNameSlug(athlete.nameSlug).build())
        .collect(Collectors.toList());

    athleteMedalSearchRepository.saveAll(athleteMedalSearches);

    return updatedAthletes.parallelStream()
        .map(
            athlete -> AthleteMedalsDTO.builder()
                .name(athlete.name)
                .url(athlete.url)
                .bronzeMedalsCount(athlete.bronzeMedalsCount)
                .silverMedalsCount(athlete.silverMedalsCount)
                .goldMedalsCount(athlete.goldMedalsCount)
                .participationsCount(athlete.participationsCount)
                .build())
        .collect(Collectors.toList());
  }


  public Answers getAnswers() {
    final double percentageOfBrAthletlesWithMedals = athleteRepository.percentageOfAthletesWithMedals("BRA");
    final double percentageOfUsaAthletlesWithMedals = athleteRepository.percentageOfAthletesWithMedals("USA");

    final double percentageOfBrAthletlesWithGoldMedals = athleteRepository.percentageOfAthletesWithGoldMedals("BRA");
    final double percentageOfUsaAthletlesWithGoldMedals = athleteRepository.percentageOfAthletesWithGoldMedals("USA");

    final double brAthletesAverageOlympicsParticipation = athleteRepository.athletesAverageOlympicsParticipation("BRA");
    final double usaAthletesAverageOlympicsParticipation = athleteRepository.athletesAverageOlympicsParticipation("USA");

    final int brTotalSportsParticipation = athleteRepository.totalSportsParticipation("BRA");
    final int usaTotalSportsParticipation = athleteRepository.totalSportsParticipation("USA");

    final int brAthletesWithMedals = athleteRepository.totalOfAthletesWithMedals("BRA");
    final int usaAthletesWithMedals = athleteRepository.totalOfAthletesWithMedals("USA");

    return Answers.builder()
        .percentageOfBrAthletlesWithMedals(percentageOfBrAthletlesWithMedals)
        .percentageOfUsaAthletlesWithMedals(percentageOfUsaAthletlesWithMedals)
        .percentageOfBrAthletlesWithGoldMedals(percentageOfBrAthletlesWithGoldMedals)
        .percentageOfUsaAthletlesWithGoldMedals(percentageOfUsaAthletlesWithGoldMedals)
        .brAthletesAverageOlympicsParticipation(brAthletesAverageOlympicsParticipation)
        .usaAthletesAverageOlympicsParticipation(usaAthletesAverageOlympicsParticipation)
        .brTotalSportsParticipation(brTotalSportsParticipation)
        .usaTotalSportsParticipation(usaTotalSportsParticipation)
        .totalOfBrAthletesWithMedals(brAthletesWithMedals)
        .totalOfUsaAthletesWithMedals(usaAthletesWithMedals)
        .build();
  }
}
