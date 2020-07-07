package net.crytec.inventoryapi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryAPIListener implements Listener {

  private InventoryManager manager;
  private JavaPlugin host;

  protected InventoryAPIListener() {
  }

  protected InventoryAPIListener(final InventoryManager manager, final JavaPlugin host) {
    this.manager = manager;
    this.host = host;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryClick(final InventoryClickEvent e) {
    final Player p = (Player) e.getWhoClicked();

    if (!manager.getInventories().containsKey(p)) {
      return;
    }

    if (e.getClickedInventory() == null || e.getAction() == InventoryAction.NOTHING) {
      e.setCancelled(true);
      return;
    }

    if (e.getClickedInventory().equals(p.getOpenInventory().getBottomInventory())) {
      manager.getInventories().get(p).getProvider().onBottomClick(e);
      if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
        e.setCancelled(true);
        return;
      }
    }

    if (e.getClickedInventory() == p.getOpenInventory().getTopInventory()) {
      e.setCancelled(true);

      final int row = e.getSlot() / 9;
      final int column = e.getSlot() % 9;

      if (row < 0 || column < 0) {
        return;
      }

      final SmartInventory inv = manager.getInventories().get(p);

      if (row >= inv.getRows() || column >= inv.getColumns()) {
        return;
      }

      manager.getContents().get(p).get(row, column).ifPresent(item -> item.run(e));

      p.updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryDrag(final InventoryDragEvent e) {
    final Player p = (Player) e.getWhoClicked();

    if (!manager.getInventories().containsKey(p)) {
      return;
    }

    final int invSize = p.getOpenInventory().getTopInventory().getSize();
    for (final int slot : e.getRawSlots()) {
      if (slot >= invSize) {
        continue;
      }
      e.setCancelled(true);
      break;
    }

    if (!e.isCancelled()) {
      manager.getInventories().get(p).getProvider().onBottomDrag(e);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryClose(final InventoryCloseEvent e) {
    final Player p = (Player) e.getPlayer();

    if (!manager.getInventories().containsKey(p)) {
      return;
    }

    final SmartInventory inv = manager.getInventories().get(p);
    inv.getProvider().onClose(p, manager.getContents().get(p));

//    if (inv.isCloseable()) {
//      e.getInventory().clear();

    manager.getInventories().remove(p);
    manager.getContents().remove(p);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerQuit(final PlayerQuitEvent e) {
    final Player p = e.getPlayer();

    if (!manager.getInventories().containsKey(p)) {
      return;
    }

    final SmartInventory inv = manager.getInventories().get(p);
    inv.getProvider().onClose(p, manager.getContents().get(p));

    manager.getInventories().remove(p);
    manager.getContents().remove(p);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPluginDisable(final PluginDisableEvent e) {
    if (!e.getPlugin().getName().equals(host.getName())) {
      return;
    }

    manager.getInventories().clear();
    manager.getContents().clear();
  }

}