package org.zhbot.daeyalt_essence_mine;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DaeyaltEssenceMineTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DaeyaltEssenceMinePlugin.class);
		RuneLite.main(args);
	}
}