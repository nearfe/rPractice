package com.conaxgames.util.finalutil;

import com.google.common.base.Joiner;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class StringUtil {

	public static final String NO_PERMISSION = CC.RED + "No permission.";
	public static final char NICE_CHAR = '●';

	public static final String IP_BAN =
			CC.RED + "Your account has been suspended from the Bloom Network! " +
					"\n" +
					"\n" + CC.RED + " To appeal, visit www.minion.lol/appeal." +
					"\n" +
					"\n" + CC.GRAY + " You may also purchase an unban at shop.minion.lol";
	public static final String IP_BAN_OTHER =
			CC.RED + "Your account has been banned from the Bloom Network!" +
					"\n" +
					"\n" + CC.RED + " This punishment is in relation to %s. " +
					"\n" + CC.RED + " To appeal, visit www.minion.lol." +
					"\n" +
					"\n" + CC.GRAY + " You may also purchase an unban at shop.minion.lol";

	public static final String PERMANENT_BAN =
			CC.RED + "Your account has been suspended from the Bloom Network!" +
					"\n" +
					"\n" + CC.RED + " To appeal, visit www.minion.lol/appeal." +
					"\n" +
					"\n" + CC.GRAY + " You may also purchase an unban at shop.minion.lol";
	public static final String TEMPORARY_BAN =
			CC.RED + "Your account has been temporarily suspended from the Bloom Network for %s!" +
					"\n" +
					"\n" + CC.RED + " To appeal, visit www.minion.lol/appeal." +
					"\n" +
					"\n" + CC.GRAY + " You may also purchase an unban at shop.minion.lol";

	public static final String PERMANENT_MUTE = CC.RED + "You are permanently muted.";
	public static final String TEMPORARY_MUTE = CC.RED + "You are temporarily muted for %s.";

	public static final String BLACKLIST =
			CC.RED + "Your account has been blacklisted from the Bloom Network." +
					"\n" +
					"\n" + CC.RED + "This punishment cannot be appealed.";

	public static final String COMMAND_COOLDOWN = CC.RED + "You must wait before sending another command.";
	
	public static final String SLOW_CHAT = CC.RED + "You cannot speak for another %time%.";

	public static final String PLAYER_ONLY = CC.RED + "Only players can use this command.";

	public static final String CHAT_COOLDOWN = CC.RED + "You must wait before sending another chat message.";

	public static final String PLAYER_NOT_FOUND = CC.RED + "Failed to find that player.";

	public static final String LOAD_ERROR = CC.RED +
			"An error occurred while loading your player data. Try again later.\n" + CC.RED + "If you still have this issue, contact a staff member";

	public static final String SPLIT_PATTERN = Pattern.compile("\\s").pattern();

	public static final FontRenderer FONT_RENDERER = new FontRenderer();

	private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";

	private static final List<String> VOWELS = Arrays.asList("a", "e", "u", "i", "o");

	private static ThreadLocal<DecimalFormat> seconds = ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));
	private static ThreadLocal<DecimalFormat> trailing = ThreadLocal.withInitial(() -> new DecimalFormat("0"));

	private StringUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static String[] formatPrivateMessage(String from, String to, String message) {
		String toMessage = CC.GRAY + "(To " + to + CC.GRAY + ") " + message;
		String fromMessage = CC.GRAY + "(From " + from + CC.GRAY + ") " + message;
		return new String[] {toMessage, fromMessage};
	}

	public static String getBorderLine() {
		int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH) / 10 * 9;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 100; i++) {
			sb.append("-");
			if (FONT_RENDERER.getWidth(sb.toString()) >= chatWidth) {
				break;
			}
		}

		return CC.SECONDARY + CC.S + sb.toString();
	}

	public static String center(String string) {
		StringBuilder preColors = new StringBuilder();
		while (string.startsWith(ChatColor.COLOR_CHAR + "")) {
			preColors.append(string.substring(0, 2));
			string = string.substring(2, string.length());
		}

		int width = FONT_RENDERER.getWidth(string);
		int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH);

		if (width == chatWidth) {
			return string;
		} else if (width > chatWidth) {
			String[] words = string.split(" ");
			if (words.length == 1) {
				return string;
			}

			StringBuilder sb = new StringBuilder();
			int total = 0;
			for (String word : words) {
				int wordWidth = FONT_RENDERER.getWidth(word + " ");
				if (total + wordWidth > chatWidth) {
					sb.append("\n");
					total = 0;
				}
				total += wordWidth;
				sb.append(word).append(" ");
			}
			return center(preColors + sb.toString().trim());
		}

		StringBuilder sb = new StringBuilder();

		int diff = (chatWidth) - (width);
		diff /= 3;

		for (int i = 0; i < 100; i++) {
			sb.append(" ");
			if (FONT_RENDERER.getWidth(sb.toString()) >= diff) {
				break;
			}
		}

		sb.append(string);

		return preColors + sb.toString();
	}

	public static String toNiceString(String string) {
		string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.toCharArray().length; i++) {
			char c = string.toCharArray()[i];
			if (i > 0) {
				char prev = string.toCharArray()[i - 1];
				if (prev == ' ' || prev == '[' || prev == '(') {
					if (i == string.toCharArray().length - 1 || c != 'x' ||
							!Character.isDigit(string.toCharArray()[i + 1])) {
						c = Character.toUpperCase(c);
					}
				}
			} else {
				if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
					c = Character.toUpperCase(c);
				}
			}
			sb.append(c);
		}

		return sb.toString();
	}

	public static String buildMessage(String[] args, int start) {
		if (start >= args.length) {
			return "";
		}
		return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
	}

	public static String getFirstSplit(String s) {
		return s.split(SPLIT_PATTERN)[0];
	}

	public static String getAOrAn(String input) {
		return ((VOWELS.contains(input.substring(0, 1).toLowerCase())) ? "an" : "a");
	}

	public static String niceTime(int i) {
		int r = i * 1000;
		int sec = r / 1000 % 60;
		int min = r / 60000 % 60;
		int h = r / 3600000 % 24;
		return (h > 0 ? (h < 10 ? "0" : "") + h + ":" : "") + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
	}

	public static String niceTime(long millis, boolean milliseconds) {
		return niceTime(millis, milliseconds, true);
	}

	public static String niceTime(long duration, boolean milliseconds, boolean trail) {
		if(milliseconds && duration < TimeUnit.MINUTES.toMillis(1)) {
			return (trail ? trailing : seconds).get().format((double) duration * 001) + 's';
		}

		return DurationFormatUtils.formatDuration(duration, (duration >= TimeUnit.HOURS.toMillis(1) ? "HH:" : "") + "mm:ss");
	}

	public static boolean handleParseInteger(CommandSender sender, String input) {
		try {
			Integer.parseInt(input);
			return false;
		} catch (NumberFormatException e) {
			sender.sendMessage(CC.RED + "Failed to parse integer.");
			return true;
		}
	}

	public static long handleParseTime(String input) {
		if(Character.isLetter(input.charAt(0)) || input.isEmpty()) {
			return -1L;
		}

		long result = 0L;

		StringBuilder number = new StringBuilder();

		// Can't do lambda here because of variable
		for(int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);

			if(Character.isDigit(c)) {
				number.append(c);
			} else {
				String str;

				if(Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
					result += handleConvert(Integer.parseInt(str), c);
					number = new StringBuilder();
				}
			}
		}

		return result;
	}

	private static long handleConvert(int value, char charType) {
		switch(charType) {
			case 'y':
				return value * TimeUnit.DAYS.toMillis(365L);
			case 'M':
				return value * TimeUnit.DAYS.toMillis(30L);
			case 'w':
				return value * TimeUnit.DAYS.toMillis(7L);
			case 'd':
				return value * TimeUnit.DAYS.toMillis(1L);
			case 'h':
				return value * TimeUnit.HOURS.toMillis(1L);
			case 'm':
				return value * TimeUnit.MINUTES.toMillis(1L);
			case 's':
				return value * TimeUnit.SECONDS.toMillis(1L);
			default:
				return -1L;
		}
	}

	public static String[] niceLore(String text, ChatColor color) {
		int dif = 32;
		String first = String.valueOf(color), second = String.valueOf(color), third = String.valueOf(color);

		if(!(text.length() > dif)) {
			return new String[] { color + text };
		}

		if(text.length() > dif * 2) {
			first += text.substring(0, dif - 1);
			second += text.substring(dif, dif * 2);
			third += text.substring(dif * 2, text.length());

			return new String[] { first, second, third };
		} else if(text.length() > dif) {
			first += text.substring(0, dif - 1);
			second += text.substring(dif - 1, text.length());

			return new String[] { first, second };
		}

		return new String[0];
	}

	public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd) {
		return andJoin(collection, delimiterBeforeAnd, ", ");
	}

	public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd, String delimiter) {
		if(collection != null && !collection.isEmpty()) {
			List<String> contents = new ArrayList<>(collection);
			String last = contents.remove(contents.size() - 1);
			StringBuilder builder = new StringBuilder(Joiner.on(delimiter).join(contents));

			if(delimiterBeforeAnd) {
				builder.append(delimiter);
			}

			return builder.append(" and ").append(last).toString();
		}

		return "";
	}
}
