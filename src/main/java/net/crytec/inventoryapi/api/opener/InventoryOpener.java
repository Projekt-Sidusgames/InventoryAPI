package net.crytec.inventoryapi.api.opener;

import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {

  Inventory open(SmartInventory inv, Player player);

  boolean supports(InventoryType type);

  default void fill(final Inventory handle, final InventoryContent contents) {
    final ClickableItem[][] items = contents.all();

    for (int row = 0; row < items.length; row++) {
      for (int column = 0; column < items[row].length; column++) {
        if (items[row][column] != null) {
          handle.setItem(9 * row + column, items[row][column].getItem());
        }
      }
    }
  }

}