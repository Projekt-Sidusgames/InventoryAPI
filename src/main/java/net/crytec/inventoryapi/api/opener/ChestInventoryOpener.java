package net.crytec.inventoryapi.api.opener;

import com.google.common.base.Preconditions;
import net.crytec.inventoryapi.InventoryManager;
import net.crytec.inventoryapi.SmartInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ChestInventoryOpener implements InventoryOpener {

  @Override
  public Inventory open(final SmartInventory inv, final Player player) {
    Preconditions.checkArgument(inv.getColumns() == 9, "The column count for the chest inventory must be 9, found: %s.", inv.getColumns());
    Preconditions.checkArgument(inv.getRows() >= 1 && inv.getRows() <= 6, "The row count for the chest inventory must be between 1 and 6, found: %s", inv.getRows());

    final InventoryManager manager = InventoryManager.get();
    final Inventory handle = manager.getContents(player).get().getInventory();

    fill(handle, manager.getContents(player).get());

    player.openInventory(handle);
    return handle;
  }

  @Override
  public boolean supports(final InventoryType type) {
    return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST;
  }

}
