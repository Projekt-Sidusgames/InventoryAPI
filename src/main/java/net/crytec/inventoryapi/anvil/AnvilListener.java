package net.crytec.inventoryapi.anvil;

import com.google.common.collect.Maps;
import java.util.Map;
import net.crytec.inventoryapi.anvil.AnvilGUI.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilListener implements Listener {

  private static final Map<Player, AnvilGUI> openInventories = Maps.newHashMap();


  protected AnvilListener() {
  }

  public void add(final Player player, final AnvilGUI gui) {
    openInventories.put(player, gui);
  }

  public void remove(final Player player) {
    openInventories.remove(player);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent e) {
    final Player clicker = (Player) e.getWhoClicked();
    if (!openInventories.containsKey(clicker)) {
      return;
    }

    final Inventory inventory = openInventories.get(clicker).getInventory();

    if (!e.getInventory().equals(inventory)) {
      return;
    }

    e.setCancelled(true);
    if (e.getRawSlot() != Slot.OUTPUT) {
      return;
    }

    final ItemStack clicked = inventory.getItem(e.getRawSlot());
    final AnvilGUI gui = openInventories.get(clicker);
    if (clicked == null || clicked.getType() == Material.AIR) {
      return;
    }

    final String ret = gui.getBiFunction().apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());

    if (ret != null) {
      final ItemMeta meta = clicked.getItemMeta();
      meta.setDisplayName(ret);
      clicked.setItemMeta(meta);
      inventory.setItem(e.getRawSlot(), clicked);
    } else {
      gui.closeInventory();
    }
  }

  @EventHandler
  public void onInventoryClose(final InventoryCloseEvent e) {
    if (!openInventories.containsKey((Player) e.getPlayer())) {
      return;
    }

    final AnvilGUI gui = openInventories.get((Player) e.getPlayer());

    if (gui.isOpen() && e.getInventory().equals(gui.getInventory())) {
      gui.closeInventory();
      openInventories.remove((Player) e.getPlayer());
    }
  }

}
