package org.zhbot.daeyalt_essence_mine;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import java.awt.*;

public class DaeyaltEssenceMineOverlay extends Overlay {
    private static final int Z_OFFSET = 200;
    private static final int MAX_DISTANCE = 2550;

    private static final int MIN_DESPAWN_TICK = 92;
    private static final int MAX_DESPAWN_TICK = 110;

    private final Client client;
    private final DaeyaltEssenceMinePlugin plugin;
    private final DaeyaltEssenceMineConfig config;
    private final SkillIconManager skillIconManager;

    @Inject
    private DaeyaltEssenceMineOverlay(Client client, DaeyaltEssenceMinePlugin plugin, DaeyaltEssenceMineConfig config, SkillIconManager skillIconManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.skillIconManager = skillIconManager;

        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        GameObject activeDaeyaltEssence = plugin.getActiveDaeyaltEssence();
        if (activeDaeyaltEssence == null)
            return null;

        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        if (activeDaeyaltEssence.getLocalLocation().distanceTo(playerLocation) < MAX_DISTANCE)
        {
            if (config.showDaeyaltEssenceClickbox())
            {
                Shape clickbox = activeDaeyaltEssence.getClickbox();
                Point mousePosition = client.getMouseCanvasPosition();
                OverlayUtil.renderHoverableArea(graphics, clickbox, mousePosition, plugin.getClickboxFillColorMinable(), plugin.getClickboxBorderColorMinable(), plugin.getClickboxBorderHoverColorMinable());
            }
            if (config.showDaeyaltEssenceIndicator())
            {
                LocalPoint gameObjectLocation = activeDaeyaltEssence.getLocalLocation();
                OverlayUtil.renderImageLocation(client, graphics, gameObjectLocation, skillIconManager.getSkillImage(Skill.MINING, false), Z_OFFSET);
            }
            if (config.showDaeyaltEssenceTimer())
            {
                float spawnedTicks = client.getTickCount() - plugin.getActiveDaeyaltEssenceSpawnTick();
                Color colour = spawnedTicks < MIN_DESPAWN_TICK ? new Color(0, 255, 0) : new Color(233, 213, 2);

                ProgressPieComponent pie = new ProgressPieComponent();
                pie.setPosition(activeDaeyaltEssence.getCanvasLocation(0));
                pie.setProgress(spawnedTicks / MAX_DESPAWN_TICK);
                pie.setBorderColor(colour.darker());
                pie.setFill(colour);
                pie.render(graphics);
            }
        }

        return null;
    }
}
