package net.crytec.inventoryapi.anvil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilAPI {


  public AnvilAPI(final JavaPlugin host) {
    AnvilGUI.WRAPPER = new AnvilImplementation();
    AnvilGUI.listener = new AnvilListener();
    Bukkit.getPluginManager().registerEvents(AnvilGUI.listener, host);
  }
}