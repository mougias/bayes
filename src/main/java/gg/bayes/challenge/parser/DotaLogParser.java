package gg.bayes.challenge.parser;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DotaLogParser {

	private final String[] lines;
	private int counter = 0;

	public DotaLogParser(String payload) {
		lines = payload.split("\n");
	}

	public DotaLogEvent nextEvent() {
		do {
			log.trace(lines[counter]);
			DotaLogEvent event = parseLine(lines[counter]);
			counter++;
			if (event != null) {
				return event;
			}
		} while (counter < lines.length);
		return null;
	}

	private DotaLogEvent parseLine(String line) {
		if (StringUtils.isBlank(line)) {
			return null;
		}
		String[] parts = line.split(" ");

		// timestamp
		DotaLogEvent event = new DotaLogEvent();
		event.setTimestamp(parseTime(parts[0]));
		if (event.getTimestamp() == 0) {
			return null;
		}

		// hero performing action
		if (parts.length < 2) {
			return null;
		} else {
			event.setHero(parseHero(parts[1]));
		}
		if (event.getHero() == null) {
			return null;
		}

		// action
		if (parts[2].equals("hits")) {
			event.setType(DotaLogEventType.HERO_DAMAGE);
			event.setTarget(parseHero(parts[3]));
			event.setAmount(Integer.valueOf(parts[7]));
			return event;
		} else if (parts[2].equals("buys") && parts[3].equals("item")) {
			event.setType(DotaLogEventType.ITEM_PURCHASE);
			event.setTarget(parseItem(parts[4]));
			return event;
		} else if (parts[2].equals("casts") && parts[3].equals("ability")) {
			event.setType(DotaLogEventType.SPELL_CAST);
			event.setTarget(parts[4].trim());
			return event;
		} else if (parts[2].equals("is") && parts[3].equals("killed") && parts[4].equals("by")) {
			event.setType(DotaLogEventType.HERO_KILL);
			event.setTarget(event.getHero());
			event.setHero(parseHero(parts[5]));
			if (event.getHero() == null) {
				// parseHero will return null for suicides, tower kills, etc
				return null;
			}
			return event;
		}

		return null;
	}

	private long parseTime(String time) {
		long timestamp = 0;
		if (!time.matches("^\\[\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}\\]$")) {
			return timestamp;
		}

		timestamp += Integer.valueOf(time.substring(1, 3)) * 60 * 60 * 1000;
		timestamp += Integer.valueOf(time.substring(4, 6)) * 60 * 1000;
		timestamp += Integer.valueOf(time.substring(7, 9)) * 1000;
		timestamp += Integer.valueOf(time.substring(11, time.length() - 1));

		return timestamp;
	}

	private String parseHero(String part) {
		part = part.trim();
		if (!part.matches("^npc_dota_hero_\\w+$")) {
			return null;
		}

		return part.substring(14);
	}

	private String parseItem(String part) {
		part = part.trim();
		if (!part.matches("^item_\\w+$")) {
			return null;
		}
		return part.substring(5);
	}
}
