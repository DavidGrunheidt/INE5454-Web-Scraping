package com.ordine.olympics.controllers;

import com.ordine.olympics.model.olympics.dto.AthleteMedalsDTO;
import com.ordine.olympics.model.olympics.dto.AthletesDTO;
import com.ordine.olympics.services.AthletesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AthletesController {
  private final AthletesService athletesService;

  @GetMapping("/athletes")
  public AthletesDTO scrapeAthletes() {
    return athletesService.scrapeAthletes();
  }

  @GetMapping("/medals")
  public List<AthleteMedalsDTO> scrapeMedals() {
    return athletesService.scrapeAthletesMedals();
  }

}
