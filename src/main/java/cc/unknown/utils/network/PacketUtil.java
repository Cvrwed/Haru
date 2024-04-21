package cc.unknown.utils.network;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import cc.unknown.mixin.interfaces.network.INetHandlerPlayClient;
import cc.unknown.mixin.interfaces.network.INetworkManager;
import cc.unknown.utils.Loona;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public class PacketUtil implements Loona {
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
	
    public static void sendPacketNoEvent(Packet<?> i) {
        ((INetworkManager)mc.getNetHandler().getNetworkManager()).sendPacketNoEvent(i);
     }
    
    public static void receivePacketNoEvent(final Packet<INetHandler> i) {
        ((INetHandlerPlayClient) mc.getNetHandler()).receiveQueueNoEvent(i);
    }
    
    public static void send(Packet<?> i) {
        mc.getNetHandler().addToSendQueue(i);
    }
    
	public static void send(Packet<?>[] i) {
        NetworkManager netManager = mc.getNetHandler() != null ? mc.getNetHandler().getNetworkManager() : null;
        if (netManager != null && netManager.isChannelOpen()) {
            netManager.flushOutboundQueue();
            for (Packet<?> packet : i) {
                netManager.dispatchPacket(packet, null);
            }
        } else if (netManager != null) {
            try {
                netManager.field_181680_j.writeLock().lock();
                for (Packet<?> packet : i) {
                    netManager.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packet, Arrays.asList((GenericFutureListener<? extends Future<? super Void>>) null).toArray(new GenericFutureListener[0])));
                }
            } finally {
                netManager.field_181680_j.writeLock().unlock();
            }
        }
    }
	
    public static void packetDelay(final Packet<?> packet) {
        packets.add(new TimedPacket(packet));
    }
    
    public static boolean packets(final Packet<?> packet) {
        return packet instanceof S0EPacketSpawnObject || packet instanceof S11PacketSpawnExperienceOrb || packet instanceof S2CPacketSpawnGlobalEntity || packet instanceof S0FPacketSpawnMob || packet instanceof S3BPacketScoreboardObjective || packet instanceof S10PacketSpawnPainting || packet instanceof S0CPacketSpawnPlayer || packet instanceof S0BPacketAnimation || packet instanceof S37PacketStatistics || packet instanceof S25PacketBlockBreakAnim || packet instanceof S36PacketSignEditorOpen || packet instanceof S35PacketUpdateTileEntity || packet instanceof S24PacketBlockAction || packet instanceof S23PacketBlockChange || packet instanceof S02PacketChat || packet instanceof S3APacketTabComplete || packet instanceof S22PacketMultiBlockChange || packet instanceof S34PacketMaps || packet instanceof S32PacketConfirmTransaction || packet instanceof S2EPacketCloseWindow || packet instanceof S30PacketWindowItems || packet instanceof S2DPacketOpenWindow || packet instanceof S31PacketWindowProperty || packet instanceof S2FPacketSetSlot || packet instanceof S3FPacketCustomPayload || packet instanceof S0APacketUseBed || packet instanceof S19PacketEntityStatus || packet instanceof S1BPacketEntityAttach || packet instanceof S27PacketExplosion || packet instanceof S2BPacketChangeGameState || packet instanceof S00PacketKeepAlive || packet instanceof S21PacketChunkData || packet instanceof S26PacketMapChunkBulk || packet instanceof S28PacketEffect || packet instanceof S14PacketEntity || packet instanceof S08PacketPlayerPosLook || packet instanceof S2APacketParticles || packet instanceof S39PacketPlayerAbilities || packet instanceof S38PacketPlayerListItem || packet instanceof S13PacketDestroyEntities || packet instanceof S1EPacketRemoveEntityEffect || packet instanceof S07PacketRespawn || packet instanceof S19PacketEntityHeadLook || packet instanceof S09PacketHeldItemChange || packet instanceof S3DPacketDisplayScoreboard || packet instanceof S1CPacketEntityMetadata || packet instanceof S12PacketEntityVelocity || packet instanceof S04PacketEntityEquipment || packet instanceof S1FPacketSetExperience || packet instanceof S06PacketUpdateHealth || packet instanceof S3EPacketTeams || packet instanceof S3CPacketUpdateScore || packet instanceof S05PacketSpawnPosition || packet instanceof S03PacketTimeUpdate || packet instanceof S33PacketUpdateSign || packet instanceof S29PacketSoundEffect || packet instanceof S0DPacketCollectItem || packet instanceof S18PacketEntityTeleport || packet instanceof S20PacketEntityProperties || packet instanceof S1DPacketEntityEffect || packet instanceof S42PacketCombatEvent || packet instanceof S41PacketServerDifficulty || packet instanceof S43PacketCamera || packet instanceof S44PacketWorldBorder || packet instanceof S45PacketTitle || packet instanceof S46PacketSetCompressionLevel || packet instanceof S47PacketPlayerListHeaderFooter || packet instanceof S48PacketResourcePackSend || packet instanceof S49PacketUpdateEntityNBT;
    }
}
