package net.crytec.inventoryapi.api;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickableItem {

  private ItemStack item;
  private Consumer<InventoryClickEvent> consumer;

  public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
    this.item = item;
    this.consumer = consumer;
  }

  public static ClickableItem empty(ItemStack item) {
    return of(item, e -> {
    });
  }

  public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
    return new ClickableItem(item, consumer);
  }

  public void run(InventoryClickEvent e) {
    consumer.accept(e);
  }

  public ItemStack getItem() {
    return item;
  }
}