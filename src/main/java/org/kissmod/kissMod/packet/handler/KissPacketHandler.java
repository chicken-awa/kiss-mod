package org.kissmod.kissMod.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.kissmod.kissMod.KissModPlugin;
import org.kissmod.kissMod.extension.ByteArrayDataExtension;
import org.kissmod.kissMod.packet.KissPacketManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@ExtensionMethod(ByteArrayDataExtension.class)
public class KissPacketHandler implements PacketHandler {

    @Override
    public void handle(Player sender, ByteArrayDataInput buf) {
        KissModPlugin plugin = KissModPlugin.getInstance();

        UUID kissEntityUuid = buf.readUuid();
        Entity kissEntity = plugin.getServer().getEntity(kissEntityUuid);
        double patVisibilityRadius = plugin.getServer().getViewDistance();

        List<Player> nearbyPlayers = null;
        if (kissEntity != null) {
            nearbyPlayers = new ArrayList<>(kissEntity
                    .getNearbyEntities(patVisibilityRadius, patVisibilityRadius, patVisibilityRadius)
                    .stream()
                    .flatMap((entity) -> {
                        if (entity instanceof Player player) {
                            return Stream.of(player);
                        }
                        return Stream.empty();
                    }).toList());
        }

        if (kissEntity instanceof Player player) {
            nearbyPlayers.add(player);
        }

        if (nearbyPlayers != null) {
            for (Player player : nearbyPlayers) {
                if (player.getUniqueId().equals(sender.getUniqueId())) {
                    continue;
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUuid(kissEntityUuid);
                out.writeUuid(sender.getUniqueId());

                player.sendPluginMessage(plugin, KissPacketManager.KISS_S2C_PACKET_ID, out.toByteArray());
            }
        }
    }


    @Override
    public String getPacketID() {
        return KissPacketManager.KISS_C2S_PACKET_ID;
    }
}