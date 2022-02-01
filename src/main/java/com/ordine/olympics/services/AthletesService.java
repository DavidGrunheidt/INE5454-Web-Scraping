package com.ordine.olympics.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordine.olympics.commons.OlympicsApiRouter;
import com.ordine.olympics.model.olympics.api.AthleteResponse;
import com.ordine.olympics.model.olympics.api.AthletesResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthletesService {

  private String buildAthletesApiQueryUrl(String query, int querySize, int skip) {
    final String apiQueryPath = String.format(OlympicsApiRouter.searchAthletes, query, querySize, skip);
    return String.format("%s%s", OlympicsApiRouter.baseUrl, apiQueryPath);
  }

  @SuppressWarnings("unchecked")
  public AthletesResponse scrapeAthletes() {
    final Random rand = new Random();
    final RestTemplate restTemplate = new RestTemplate();
    final ObjectMapper mapper = new ObjectMapper();
    final List<AthleteResponse> response = new ArrayList<>();

    final int querySize = 1000;
    final int maxSkip = 100000; // Limitation of the API. We can only skip until 10^6
    final int skip = rand.nextInt((maxSkip / querySize) + 1) * querySize;

    assert(skip % querySize == 0 && skip < maxSkip);

    final List<String> desiredCountryCodes = Arrays.asList("BRA", "USA"); // Only BR and USA athletes;
    final String url = buildAthletesApiQueryUrl("", querySize, skip);
    log.info(String.format("GET - %s", url));

    final String resultRaw = restTemplate.getForObject(url, String.class);
    try {
      final Map<String, Object> resultMap = mapper.readValue(resultRaw, Map.class);
      final List<Map<String, Object>> modules = mapper.convertValue(resultMap.get("modules"), List.class);
      final AthletesResponse athletesRaw = mapper.convertValue(modules.get(0), AthletesResponse.class);

      final List<AthleteResponse> desiredAthletes = athletesRaw.athletes.stream()
          .filter(athlete -> desiredCountryCodes.contains(athlete.countryCode) && athlete.discipline != null && !athlete.discipline.isBlank())
          .collect(Collectors.toList());

      response.addAll(desiredAthletes);
    } catch (JsonProcessingException e) {
      log.error(String.valueOf(e));
    }

    return new AthletesResponse(response);
  }

}
