package tonius.simplyjetpacks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import tonius.simplyjetpacks.item.jetpack.JetpackParticleType;
import tonius.simplyjetpacks.tickhandler.PlayerTickHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy {

    public void registerHandlers() {
        SimplyJetpacks.logger.info("Registering handlers");
        FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
        FMLCommonHandler.instance().bus().register(new SyncTracker());
    }

    public void showJetpackParticles(World world, EntityLivingBase wearer, JetpackParticleType particle) {
    }

}
