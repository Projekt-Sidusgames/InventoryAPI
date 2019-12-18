package net.crytec.inventoryapi;

import net.crytec.inventoryapi.anvil.AnvilListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryAPI {

  private static InventoryAPI api;

  private final JavaPlugin host;
  private final InventoryManager manager;

  public InventoryAPI(final JavaPlugin host) {
    this(host, true);
  }

  public InventoryAPI(final JavaPlugin host, final boolean useAnvilAPI) {
    InventoryAPI.api = this;
    this.host = host;
    manager = new InventoryManager();

    Bukkit.getPluginManager().registerEvents(new InventoryAPIListener(manager, host), host);
    if (useAnvilAPI) {
      Bukkit.getPluginManager().registerEvents(new AnvilListener(), host);
    }
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


}
