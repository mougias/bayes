package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.rest.exception.NoSuchHeroException;
import gg.bayes.challenge.rest.exception.NoSuchMatchException;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

	private final MatchService matchService;

	@Autowired
	public MatchController(MatchService matchService) {
		this.matchService = matchService;
	}

	@PostMapping(consumes = "text/plain")
	public ResponseEntity<Long> ingestMatch(@RequestBody @NotNull @NotBlank String payload) {
		final Long matchId = matchService.ingestMatch(payload);
		return ResponseEntity.ok(matchId);
	}

	@GetMapping("{matchId}")
	public ResponseEntity<List<HeroKills>> getMatch(@PathVariable("matchId") Long matchId) throws NoSuchMatchException {
		return ResponseEntity.ok(matchService.getHeroKillsForMatch(matchId));
	}

	@GetMapping("{matchId}/{heroName}/items")
	public ResponseEntity<List<HeroItems>> getItems(@PathVariable("matchId") Long matchId,
			@PathVariable("heroName") String heroName) throws NoSuchMatchException, NoSuchHeroException {
		return ResponseEntity.ok(matchService.getHeroItemsForMatch(matchId, heroName));
	}

	@GetMapping("{matchId}/{heroName}/spells")
	public ResponseEntity<List<HeroSpells>> getSpells(@PathVariable("matchId") Long matchId,
			@PathVariable("heroName") String heroName) throws NoSuchMatchException, NoSuchHeroException {
		return ResponseEntity.ok(matchService.getHeroSpellsForMatch(matchId, heroName));
	}

	@GetMapping("{matchId}/{heroName}/damage")
	public ResponseEntity<List<HeroDamage>> getDamage(@PathVariable("matchId") Long matchId,
			@PathVariable("heroName") String heroName) throws NoSuchMatchException, NoSuchHeroException {
		return ResponseEntity.ok(matchService.getHeroDamagesForMatch(matchId, heroName));
	}
}
