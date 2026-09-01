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
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;

@Slf4j
@PluginDescriptor(
	name = "Daeyalt Essence Mine"
)
public class DaeyaltEssenceMinePlugin extends Plugin
{
	private static final int DAEYALT_ESSENCE_MINE_REGION_ID = 14744;

	@Inject
	private Client client;

	@Inject
	private DaeyaltEssenceMineConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

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

	private boolean inDaeyaltEssenceMine = false;

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
		clickboxFillColorMinable = ColorUtil.colorWithAlpha(clickboxBorderColorMinable, 50);
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
		var gameState = event.getGameState();
		if (gameState != GameState.LOADING)
			return;

		activeDaeyaltEssence = null;
		activeDaeyaltEssenceSpawnTick = -1;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		var obj = event.getGameObject();

		switch (obj.getId())
		{
			case ObjectID.DAEYALT_STONE_TOP_ACTIVE:
				if (obj == activeDaeyaltEssence)
					return;

				if (activeDaeyaltEssenceSpawnTick > 0)
					notifier.notify(config.notification(), "Daeyalt Essence Mine spawned");

				activeDaeyaltEssenceSpawnTick = client.getTickCount();
				activeDaeyaltEssence = obj;
				break;
			case ObjectID.DAEYALT_STONE_TOP:

				break;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		var object = event.getGameObject();
		if (object.getId() == ObjectID.DAEYALT_STONE_TOP_ACTIVE && object == activeDaeyaltEssence)
			activeDaeyaltEssence = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		var player = client.getLocalPlayer();
		if (player == null)
		{
			inDaeyaltEssenceMine = false;
			return;
		}

		var worldLocation = player.getWorldLocation();
		if (worldLocation == null)
		{
			inDaeyaltEssenceMine = false;
			return;
		}

		var region = worldLocation.getRegionID();
		inDaeyaltEssenceMine = region == DAEYALT_ESSENCE_MINE_REGION_ID;

		if (!inDaeyaltEssenceMine)
		{
			activeDaeyaltEssence = null;
			activeDaeyaltEssenceSpawnTick = -1;
		}
	}

	@Provides
	DaeyaltEssenceMineConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DaeyaltEssenceMineConfig.class);
	}

	public boolean outsideDaeyaltEssenceMine()
	{
		return !inDaeyaltEssenceMine;
	}
}
