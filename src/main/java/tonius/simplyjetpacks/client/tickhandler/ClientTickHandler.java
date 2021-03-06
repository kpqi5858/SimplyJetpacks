package tonius.simplyjetpacks.client.tickhandler;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import tonius.simplyjetpacks.SimplyJetpacks;
import tonius.simplyjetpacks.SyncTracker;
import tonius.simplyjetpacks.item.jetpack.JetpackParticleType;
import tonius.simplyjetpacks.network.PacketHandler;
import tonius.simplyjetpacks.network.message.MessageKeyboardSync;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler {

    private static Minecraft mc = Minecraft.getMinecraft();

    private static boolean lastJumpState = false;
    private static boolean lastForwardState = false;
    private static boolean lastBackwardState = false;
    private static boolean lastLeftState = false;
    private static boolean lastRightState = false;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent evt) {
        if (evt.phase == Phase.START) {
            tickStart();
        } else {
            tickEnd();
        }
    }

    private static void tickStart() {
        if (mc.thePlayer != null) {
            boolean jumpState = mc.gameSettings.keyBindJump.getIsKeyPressed();
            boolean forwardState = mc.gameSettings.keyBindForward.getIsKeyPressed();
            boolean backwardState = mc.gameSettings.keyBindBack.getIsKeyPressed();
            boolean leftState = mc.gameSettings.keyBindLeft.getIsKeyPressed();
            boolean rightState = mc.gameSettings.keyBindRight.getIsKeyPressed();

            if (jumpState != lastJumpState || forwardState != lastForwardState || backwardState != lastBackwardState || leftState != lastLeftState || rightState != lastRightState) {
                lastJumpState = jumpState;
                lastForwardState = forwardState;
                lastBackwardState = backwardState;
                lastLeftState = leftState;
                lastRightState = rightState;
                PacketHandler.instance.sendToServer(new MessageKeyboardSync(jumpState, forwardState, backwardState, leftState, rightState));
                SyncTracker.processKeyUpdate(mc.thePlayer, jumpState, forwardState, backwardState, leftState, rightState);
            }
        }
    }

    private static void tickEnd() {
        if (mc.thePlayer != null && mc.theWorld != null && !mc.isGamePaused()) {
            Iterator<Integer> itr = SyncTracker.getJetpackStates().keySet().iterator();
            int currentEntity;
            while (itr.hasNext()) {
                currentEntity = itr.next();
                Entity entity = mc.theWorld.getEntityByID(currentEntity);
                if (entity == null || !(entity instanceof EntityLivingBase)) {
                    itr.remove();
                } else {
                    JetpackParticleType particle = SyncTracker.getJetpackStates().get(currentEntity);
                    if (particle != null) {
                        SimplyJetpacks.proxy.showJetpackParticles(mc.theWorld, (EntityLivingBase) entity, particle);
                    } else {
                        itr.remove();
                    }
                }
            }
        }
    }
}
