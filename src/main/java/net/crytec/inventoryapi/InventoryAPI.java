package net.crytec.inventoryapi;

import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.anvil.AnvilListener;
import net.crytec.inventoryapi.anvil.Response;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    Bukkit.getPluginManager().registerEvents(new AnvilListener(host), host);
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


  public void test(final Player viewer) {

    new AnvilGUI.Builder()
        .onClose(player -> player.sendMessage("Du hast das Inventar geschlossen!"))
        .onComplete((player, text) -> {
          if (text.equals("blub")) {
            player.sendMessage("Korrekte Eingabe");
            return Response.close();
          } else {
            return Response.text("Falsche Antwort, versuch es nochmal");
          }
        })
        .preventClose()
        .text("Deine Eingabe...")
        .item(new ItemStack(Material.APPLE))
        .title("Custom Title, yay :D")
        .open(viewer);


  }
}