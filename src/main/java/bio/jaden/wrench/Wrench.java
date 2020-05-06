package bio.jaden.wrench;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bio.jaden.wrench.init.ModItems;

@Mod(Wrench.MOD_ID)
public class Wrench {
    public static final String MOD_ID = "wrench";
    public static final String MOD_NAME = "Wrench";
    public static final Logger LOGGER = LogManager.getLogger();

    public Wrench() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // register deferred registries
        ModItems.ITEMS.register(modEventBus);

        // register mod event bus
        MinecraftForge.EVENT_BUS.register(this);
    }
}
