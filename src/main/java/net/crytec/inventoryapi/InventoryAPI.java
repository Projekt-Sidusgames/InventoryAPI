package net.crytec.inventoryapi;

import java.util.ArrayList;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.Pagination;
import net.crytec.inventoryapi.api.SlotIterator;
import net.crytec.inventoryapi.api.SlotIterator.Type;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryAPI {

  private static InventoryAPI api;

  private final JavaPlugin host;
  private final InventoryManager manager;

  public InventoryAPI(final JavaPlugin host) {
    InventoryAPI.api = this;
    this.host = host;
    manager = new InventoryManager();

    Bukkit.getPluginManager().registerEvents(new InventoryAPIListener(manager, host), host);
  }

  public static InventoryAPI get() {
    return api;
  }

  public InventoryManager getManager() {
    return manager;
  }

  public JavaPlugin getHost() {
    return host;
  }


//  public void test(final InventoryContent contents) {
//
//    final Pagination pagination = contents.pagination();
//    final ArrayList<ClickableItem> items = new ArrayList<>();
//
//    // Alle items aller seiten hinzufügen
//
//    pagination.setItems(c);
//    pagination.setItemsPerPage(18);
//
//    SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, SlotPos.of(1, 1));
//    slotIterator = slotIterator.allowOverride(false);
//    slotIterator.add(Pagination)
//    pagination.addToIterator(slotIterator);
//
//
//
//  }


}