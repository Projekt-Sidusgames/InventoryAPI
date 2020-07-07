package net.crytec.inventoryapi.anvil;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilListener implements Listener {

  private static final Map<Player, AnvilGUI> openInventories = new HashMap<>();
  private final JavaPlugin host;

  public static void register(final Player player, final AnvilGUI gui) {
    openInventories.put(player, gui);
  }

  public static void unregister(final Player player) {
    openInventories.remove(player);
  }

  public AnvilListener(final JavaPlugin host) {
    this.host = host;
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final Player clicker = (Player) event.getWhoClicked();
    if (!openInventories.containsKey(clicker)) {
      return;
    }
    final Inventory inventory = openInventories.get(clicker).getInventory();
    final AnvilGUI gui = openInventories.get(clicker);

    if (
        ((event.getInventory().equals(inventory)) && (event.getRawSlot() < 3)) ||
            (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) ||
            ((event.getRawSlot() < 3) && ((event.getAction().equals(InventoryAction.PLACE_ALL)) || (event.getAction().equals(InventoryAction.PLACE_ONE)) || (event.getAction()
                .equals(InventoryAction.PLACE_SOME)) || (event.getCursor() != null)))
    ) {
      event.setCancelled(true);
      if (event.getRawSlot() == AnvilSlot.OUTPUT) {
        final ItemStack clicked = inventory.getItem(AnvilSlot.OUTPUT);
        if (clicked == null || clicked.getType() == Material.AIR) {
          return;
        }

        final Response response = gui.completeFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : "");
        if (response.getText() != null) {
          final ItemMeta meta = clicked.getItemMeta();
          meta.setDisplayName(response.getText());
          clicked.setItemMeta(meta);
          inventory.setItem(AnvilSlot.INPUT_LEFT, clicked);
        } else {
          gui.closeInventory();
        }
      }
    }
  }

  @EventHandler
  public void onInventoryDrag(final InventoryDragEvent event) {
    final Player clicker = (Player) event.getWhoClicked();
    if (!openInventories.containsKey(clicker)) {
      return;
    }
    final Inventory inventory = openInventories.get(clicker).getInventory();

    if (event.getInventory().equals(inventory)) {
      for (final int slot : AnvilSlot.values()) {
        if (event.getRawSlots().contains(slot)) {
          event.setCancelled(true);
          break;
        }
      }
    }
  }

  @EventHandler
  public void onInventoryClose(final InventoryCloseEvent event) {
    final Player clicker = (Player) event.getPlayer();
    if (!openInventories.containsKey(clicker)) {
      return;
    }
    final AnvilGUI gui = openInventories.get(clicker);

    if (openInventories.get(clicker).isOpen() && event.getInventory().equals(gui.getInventory())) {
      gui.closeInventory();
      if (gui.isPreventClose()) {
        Bukkit.getScheduler().runTask(host, gui::openInventory);
      }
    }
  }

}