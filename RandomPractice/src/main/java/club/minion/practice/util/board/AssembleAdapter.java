package club.minion.practice.util.board;

import org.bukkit.entity.Player;

import java.util.List;

public interface AssembleAdapter {

	/**
	 * Gets the scoreboard title.
	 *
	 * @param player who's title is being displayed.
	 * @return title.
	 */
	String getTitle(Player player);

	/**
	 * Gets the scoreboard lines.
	 *
	 * @param player who's lines are being displayed.
	 * @return lines.
	 */
	List<String> getLines(Player player);

}
