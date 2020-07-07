package net.crytec.inventoryapi.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface InventoryProvider {

  public void init(Player player, InventoryContent content);

  public default void preInit(final Player player) {
    return;
  }

  public default void onClose(final Player player, final InventoryContent content) {
  }

  public default void reopen(final Player player, final InventoryContent content) {
    content.getHost().open(player, content.pagination().getPage());
  }

  default void onBottomClick(final InventoryClickEvent event) {
  }

  default void onBottomDrag(final InventoryDragEvent event) {
  }

}