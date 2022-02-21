package com.ordine.olympics.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordine.olympics.commons.OlympicsApiRouter;
import com.ordine.olympics.model.db.Athlete;
import com.ordine.olympics.model.olympics.dto.AthleteDTO;
import com.ordine.olympics.model.olympics.dto.AthletesDTO;
import com.ordine.olympics.repository.AthleteRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthletesService {

  @Autowired
  private AthleteRepository athleteRepository;

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

}
