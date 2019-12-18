package net.crytec.inventoryapi.api.buttons;

import java.util.function.Consumer;
import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.api.ClickableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InputButton extends ClickableItem {

  private Consumer<InventoryClickEvent> onRightClick;

  public InputButton(final Consumer<String> result) {
    this(new ItemStack(Material.BOOK), "Input", result);
  }

  public InputButton(final ItemStack icon, final String text, final Consumer<String> result) {
    this(icon, e -> {
      new AnvilGUI((Player) e.getWhoClicked(), text, (p, i) -> {
        result.accept(i);
        return null;
      });
    });
  }

  public InputButton onRightClick(final Consumer<InventoryClickEvent> consumer) {
		onRightClick = consumer;
    return this;
  }

  private InputButton(final ItemStack item, final Consumer<InventoryClickEvent> consumer) {
    super(item, consumer);
  }

  @Override
  public void run(final InventoryClickEvent e) {
    if (onRightClick != null && e.getClick() == ClickType.RIGHT) {
			onRightClick.accept(e);
      return;
    }
    super.run(e);
  }

}