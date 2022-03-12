package bio.jaden.wrench.init;

import bio.jaden.wrench.Wrench;
import bio.jaden.wrench.common.item.WrenchItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Wrench.MOD_ID);
    public static final RegistryObject<Item> WRENCH_ITEM = ITEMS.register("wrench", () -> new WrenchItem(
        new Item.Properties()
            .tab(CreativeModeTab.TAB_TOOLS)
            .defaultDurability(375)
    ));
}
