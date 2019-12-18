package net.crytec.inventoryapi.api;

import org.bukkit.entity.Player;

public interface InventoryProvider {

  public void init(Player player, InventoryContent content);

  public default void preInit(final Player player) {
    return;
  }

  public default void onClose(final Player player, final InventoryContent content) {
    return;
  }

  public default void reopen(final Player player, final InventoryContent content) {
    content.getHost().open(player, content.pagination().getPage());
  }
}