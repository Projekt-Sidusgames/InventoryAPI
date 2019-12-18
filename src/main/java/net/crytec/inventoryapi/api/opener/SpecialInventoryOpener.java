package net.crytec.inventoryapi.api.opener;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.crytec.inventoryapi.InventoryManager;
import net.crytec.inventoryapi.SmartInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SpecialInventoryOpener implements InventoryOpener {

  private static final List<InventoryType> SUPPORTED = ImmutableList.of(
      InventoryType.FURNACE,
      InventoryType.WORKBENCH,
      InventoryType.DISPENSER,
      InventoryType.DROPPER,
      InventoryType.ENCHANTING,
      InventoryType.BREWING,
      InventoryType.ANVIL,
      InventoryType.BEACON,
      InventoryType.HOPPER
  );


  @Override
  public Inventory open(final SmartInventory inv, final Player player) {
    final InventoryManager manager = InventoryManager.get();
    final Inventory handle = manager.getContents(player).get().getInventory();

    fill(handle, manager.getContents(player).get());

    player.openInventory(handle);
    return handle;
  }

  @Override
  public boolean supports(final InventoryType type) {
    return SUPPORTED.contains(type);
  }
}
