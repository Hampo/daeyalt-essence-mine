package org.zhbot.daeyalt_essence_mine;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup(DaeyaltEssenceMineConfig.group)
public interface DaeyaltEssenceMineConfig extends Config
{
	String group = "daeyalt-essence-mine";

	@ConfigItem(
		keyName = "notifications",
		name = "Notifications",
		description = "Configures all notifications"
	)
	default Notification notification()
	{
		return Notification.ON;
	}

	@ConfigItem(
			keyName = "showDaeyaltEssenceIndicator",
			name = "Show daeyalt essence indicator",
			description = "Configures whether to display an indicator when daeyalt essence is ready to be mined",
			position = 1
	)
	default boolean showDaeyaltEssenceIndicator()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDaeyaltEssenceTimer",
			name = "Show daeyalt essence timer",
			description = "Configures whether to display a timer for when a daeyalt essence is about to despawn",
			position = 1
	)
	default boolean showDaeyaltEssenceTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDaeyaltEssenceClickbox",
			name = "Show daeyalt essence click box",
			description = "Configures whether to display a click box when daeyalt essence is ready to be mined",
			position = 2
	)
	default boolean showDaeyaltEssenceClickbox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDaeyaltEssenceClickboxAvailable",
			name = "Available Color",
			description = "Configures Color of available daeyalt essence",
			position = 3
	)
	default Color showDaeyaltEssenceClickboxAvailable()
	{
		return Color.GREEN;
	}
}
