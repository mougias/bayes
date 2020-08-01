package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.jpa.model.Match;
import gg.bayes.challenge.jpa.model.MatchHero;
import gg.bayes.challenge.jpa.model.MatchHeroDamage;
import gg.bayes.challenge.jpa.repository.MatchRepository;
import gg.bayes.challenge.parser.DotaLogEvent;
import gg.bayes.challenge.parser.DotaLogParser;
import gg.bayes.challenge.rest.exception.NoSuchHeroException;
import gg.bayes.challenge.rest.exception.NoSuchMatchException;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MatchServiceImpl implements MatchService {

	private final MatchRepository matchRepository;

	@Override
	public Long ingestMatch(String payload) {
		Match match = new Match();
		match.setHeroes(new HashMap<>());

		DotaLogParser parser = new DotaLogParser(payload);

		DotaLogEvent event = parser.nextEvent();
		while (event != null) {
			log.trace(event.toString());
			switch (event.getType()) {
			case HERO_DAMAGE:
				ingestHeroDamageInstance(match, event.getHero(), event.getTarget(), event.getAmount());
				break;
			case HERO_KILL:
				ingestHeroKill(match, event.getHero(), event.getTarget());
				break;
			case ITEM_PURCHASE:
				ingestItemPurchase(match, event.getHero(), event.getTarget(), event.getTimestamp());
				break;
			case SPELL_CAST:
				ingestSpellCast(match, event.getHero(), event.getTarget());
				break;
			default:
				throw new RuntimeException("Unsupported event type " + event.getType());
			}

			event = parser.nextEvent();
		}

		matchRepository.save(match);
		return match.getId();
	}

	public List<HeroKills> getHeroKillsForMatch(Long matchId) throws NoSuchMatchException {
		Match match = matchRepository.findById(matchId).orElseThrow(NoSuchMatchException::new);
		List<HeroKills> heroKillsList = new ArrayList<>();
		match.getHeroes().forEach((name, value) -> {
			HeroKills heroKills = new HeroKills();
			heroKills.setHero(name);
			heroKills.setKills(
					value.getHeroDamages().values().stream().map(MatchHeroDamage::getKills).reduce(0, Integer::sum));
			heroKillsList.add(heroKills);
		});

		return heroKillsList;
	}

	public List<HeroItems> getHeroItemsForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException {
		Match match = matchRepository.findById(matchId).orElseThrow(NoSuchMatchException::new);
		MatchHero matchHero = match.getHeroes().get(heroName);
		if (matchHero == null) {
			throw new NoSuchHeroException();
		}

		return matchHero.getItems().entrySet().stream().map(e -> new HeroItems(e.getValue(), e.getKey()))
				.collect(Collectors.toList());
	}

	public List<HeroSpells> getHeroSpellsForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException {
		Match match = matchRepository.findById(matchId).orElseThrow(NoSuchMatchException::new);
		MatchHero matchHero = match.getHeroes().get(heroName);
		if (matchHero == null) {
			throw new NoSuchHeroException();
		}

		return matchHero.getSpellCasts().entrySet().stream().map(e -> new HeroSpells(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	public List<HeroDamage> getHeroDamagesForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException {
		Match match = matchRepository.findById(matchId).orElseThrow(NoSuchMatchException::new);
		MatchHero matchHero = match.getHeroes().get(heroName);
		if (matchHero == null) {
			throw new NoSuchHeroException();
		}

		return matchHero.getHeroDamages().entrySet().stream()
				.map(e -> new HeroDamage(e.getKey(), e.getValue().getInstances(), e.getValue().getTotalDamage()))
				.collect(Collectors.toList());
	}

	private void ingestHeroDamageInstance(Match match, String hero, String target, int amount) {
		MatchHeroDamage matchHeroDamage = getMatchHeroDamage(match, hero, target);
		matchHeroDamage.setInstances(matchHeroDamage.getInstances() + 1);
		matchHeroDamage.setTotalDamage(matchHeroDamage.getTotalDamage() + amount);
	}

	private void ingestHeroKill(Match match, String hero, String target) {
		MatchHeroDamage matchHeroDamage = getMatchHeroDamage(match, hero, target);
		matchHeroDamage.setKills(matchHeroDamage.getKills() + 1);
	}

	private void ingestItemPurchase(Match match, String hero, String item, Long purchaseTime) {
		MatchHero matchHero = match.getHeroes().get(hero);
		if (matchHero == null) {
			matchHero = createNewMatchHero(match, hero);
		}
		matchHero.getItems().put(purchaseTime, item);
		log.trace("Hero {}, item purchase at {}, item {}", hero, purchaseTime, item);
	}

	private void ingestSpellCast(Match match, String hero, String spell) {
		MatchHero matchHero = match.getHeroes().get(hero);
		if (matchHero == null) {
			matchHero = createNewMatchHero(match, hero);
		}
		if (!matchHero.getSpellCasts().containsKey(spell)) {
			matchHero.getSpellCasts().put(spell, 1);
		} else {
			matchHero.getSpellCasts().put(spell, matchHero.getSpellCasts().get(spell) + 1);
		}
	}

	private MatchHeroDamage getMatchHeroDamage(Match match, String hero, String target) {
		MatchHero matchHero = match.getHeroes().get(hero);
		if (matchHero == null) {
			matchHero = createNewMatchHero(match, hero);
		}

		MatchHeroDamage matchHeroDamage = matchHero.getHeroDamages().get(target);
		if (matchHeroDamage == null) {
			matchHeroDamage = createNewMatchHeroDamage(matchHero, target);
		}

		return matchHeroDamage;
	}

	private MatchHero createNewMatchHero(Match match, String hero) {
		MatchHero matchHero = new MatchHero();
		matchHero.setHeroDamages(new HashMap<>());
		matchHero.setSpellCasts(new HashMap<>());
		matchHero.setItems(new HashMap<>());
		matchHero.setMatch(match);
		match.getHeroes().put(hero, matchHero);
		return matchHero;
	}

	private MatchHeroDamage createNewMatchHeroDamage(MatchHero matchHero, String target) {
		MatchHeroDamage matchHeroDamage = new MatchHeroDamage();
		matchHeroDamage.setHero(matchHero);
		matchHero.getHeroDamages().put(target, matchHeroDamage);
		return matchHeroDamage;
	}
}
