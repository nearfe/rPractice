package club.minion.practice.runnable;

import club.minion.practice.Practice;
import club.minion.practice.player.PracticePlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SaveDataRunnable implements Runnable {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		for (PracticePlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
			this.plugin.getPlayerManager().saveData(playerData);
		}
	}

}
