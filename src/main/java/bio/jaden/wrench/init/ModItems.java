package bio.jaden.wrench.init;

import bio.jaden.wrench.Wrench;
import bio.jaden.wrench.common.item.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Wrench.MOD_ID);
    public static final RegistryObject<Item> WRENCH_ITEM = ITEMS.register("wrench", () -> new WrenchItem(
        new Item.Properties()
            .group(ItemGroup.TOOLS)
            .defaultMaxDamage(375)
    ));
}