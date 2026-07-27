package org.zhbot.daeyalt_essence_mine;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Daeyalt Essence Mine"
)
public class DaeyaltEssenceMinePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DaeyaltEssenceMineConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	private static final int INACTIVE_DAEYALT_ESSENCE_ID = 39094;
	private static final int ACTIVE_DAEYALT_ESSENCE_ID = 39095;

	@Inject
	private DaeyaltEssenceMineOverlay mine;

	@Getter
	private GameObject activeDaeyaltEssence;
	@Getter
	private int activeDaeyaltEssenceSpawnTick = -1;

	@Getter
	private Color clickboxBorderColorMinable;
	@Getter
	private Color clickboxFillColorMinable;
	@Getter
	private Color clickboxBorderHoverColorMinable;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(mine);

		updateConfig();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(DaeyaltEssenceMineConfig.group))
			updateConfig();
	}

	private void updateConfig()
	{
		clickboxBorderColorMinable = config.showDaeyaltEssenceClickboxAvailable();
		clickboxFillColorMinable = new Color(clickboxBorderColorMinable.getRed(), clickboxBorderColorMinable.getGreen(), clickboxBorderColorMinable.getBlue(), 50);
		clickboxBorderHoverColorMinable = clickboxBorderColorMinable.darker();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(mine);

		activeDaeyaltEssence = null;
		activeDaeyaltEssenceSpawnTick = -1;

		clickboxFillColorMinable = null;
		clickboxBorderColorMinable = null;
		clickboxBorderHoverColorMinable = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		if (gameState == GameState.LOADING)
		{
			activeDaeyaltEssence = null;
			activeDaeyaltEssenceSpawnTick = -1;
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject obj = event.getGameObject();
		int id = obj.getId();

		switch (id)
		{
			case ACTIVE_DAEYALT_ESSENCE_ID:
				if (activeDaeyaltEssenceSpawnTick >= 0)
					notifier.notify(config.notification(), "Daeyalt Essence Mine spawned");
				activeDaeyaltEssenceSpawnTick = client.getTickCount();
				activeDaeyaltEssence = obj;
				break;
			case INACTIVE_DAEYALT_ESSENCE_ID:

				break;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject object = event.getGameObject();
		if (object.getId() == ACTIVE_DAEYALT_ESSENCE_ID && object == activeDaeyaltEssence)
			activeDaeyaltEssence = null;
	}

	@Provides
	DaeyaltEssenceMineConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DaeyaltEssenceMineConfig.class);
	}
}
