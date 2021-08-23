package com.example.websocket.controller;

import com.example.websocket.dto.ScenarioDto;
import com.example.websocket.enity.Scenario;
import com.example.websocket.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioRepository scenarioRepository;

    @GetMapping("/scenarios")
    public ResponseEntity<ScenarioDto> getScenario() {
        Scenario scenario = scenarioRepository.getById(1L);

        ScenarioDto scenarioDto = new ScenarioDto();
        scenarioDto.setId(scenario.getId());
        scenarioDto.setName(scenario.getName());

        return ResponseEntity.ok(scenarioDto);
    }

    @PostMapping("/scenarios")
    public ResponseEntity<ScenarioDto> createScenario() {
        Scenario scenario = new Scenario();
        scenario.setName("test");

        scenarioRepository.save(scenario);

        ScenarioDto scenarioDto = new ScenarioDto();
        scenarioDto.setId(scenario.getId());
        scenarioDto.setName(scenario.getName());

        return ResponseEntity.ok(scenarioDto);
    }

    @MessageMapping("/update-scenario")
    @SendTo("/topic/activity")
    @Transactional
    public ScenarioDto message(ScenarioDto scenarioDto) {
        Scenario scenario = scenarioRepository.getById(1L);
        scenario.setName(scenarioDto.getName());

        return scenarioDto;
    }
}
