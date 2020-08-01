package gg.bayes.challenge.service;

import java.util.List;

import gg.bayes.challenge.rest.exception.NoSuchHeroException;
import gg.bayes.challenge.rest.exception.NoSuchMatchException;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;

public interface MatchService {
	Long ingestMatch(String payload);

	public List<HeroKills> getHeroKillsForMatch(Long matchId) throws NoSuchMatchException;

	public List<HeroItems> getHeroItemsForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException;

	public List<HeroSpells> getHeroSpellsForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException;

	public List<HeroDamage> getHeroDamagesForMatch(Long matchId, String heroName)
			throws NoSuchMatchException, NoSuchHeroException;
}
