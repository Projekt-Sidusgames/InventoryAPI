package net.crytec.inventoryapi.api;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.crytec.inventoryapi.SmartInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryContent {

  private final Player holder;
  private final SmartInventory host;
  private final ClickableItem[][] contents;

  private final Inventory inventory;
  private final Pagination pagination = new Pagination();

  private final Map<String, Object> properties = Maps.newHashMap();

  public InventoryContent(final SmartInventory inventory, final Player player) {
    holder = player;
    host = inventory;

    if (host.getType() == InventoryType.CHEST || host.getType() == InventoryType.ENDER_CHEST) {
      this.inventory = Bukkit.createInventory(player, host.getColumns() * host.getRows(), host.getTitle());
    } else {
      this.inventory = Bukkit.createInventory(player, host.getType(), host.getTitle());
    }
    contents = new ClickableItem[host.getRows()][host.getColumns()];
  }

  public SmartInventory getHost() {
    return host;
  }

  public Pagination pagination() {
    return pagination;
  }

  public SlotIterator newIterator(final SlotIterator.Type type, final SlotPos startPos) {
    return new SlotIterator(this, host, type, startPos.getRow(), startPos.getColumn());
  }

  public Optional<SlotPos> firstEmpty() {
    for (int column = 0; column < contents[0].length; column++) {
      for (int row = 0; row < contents.length; row++) {
        if (get(row, column).isEmpty()) {
          return Optional.of(new SlotPos(row, column));
        }
      }
    }
    return Optional.empty();
  }

  public ClickableItem[][] all() {
    return contents;
  }

  public Optional<ClickableItem> get(final int row, final int column) {
    if (row >= contents.length) {
      return Optional.empty();
    }
    if (column >= contents[row].length) {
      return Optional.empty();
    }

    return Optional.ofNullable(contents[row][column]);
  }

  public Optional<ClickableItem> get(final SlotPos slotPos) {
    return get(slotPos.getRow(), slotPos.getColumn());
  }

  public InventoryContent set(final int row, final int column, final ClickableItem item) {
    if (row >= contents.length) {
      return this;
    }
    if (column >= contents[row].length) {
      return this;
    }

    contents[row][column] = item;
    update(row, column, item);
    return this;
  }

  public InventoryContent set(final SlotPos slotPos, final ClickableItem item) {
    return set(slotPos.getRow(), slotPos.getColumn(), item);
  }

  public InventoryContent set(final int slot, final ClickableItem item) {
    return set(SlotPos.of(slot), item);
  }

  public InventoryContent add(final ClickableItem item) {
    for (int row = 0; row < contents.length; row++) {
      for (int column = 0; column < contents[0].length; column++) {
        if (contents[row][column] == null) {
          set(row, column, item);
          return this;
        }
      }
    }

    return this;
  }

  public InventoryContent fill(final ClickableItem item) {
    for (int row = 0; row < contents.length; row++) {
      for (int column = 0; column < contents[row].length; column++) {
        set(row, column, item);
      }
    }

    return this;
  }

  public InventoryContent fillRow(final int row, final ClickableItem item) {
    if (row >= contents.length) {
      return this;
    }

    for (int column = 0; column < contents[row].length; column++) {
      set(row, column, item);
    }

    return this;
  }

  public InventoryContent fillColumn(final int column, final ClickableItem item) {
    for (int row = 0; row < contents.length; row++) {
      set(row, column, item);
    }

    return this;
  }

  public InventoryContent fillBorders(final ClickableItem item) {
    fillRect(0, 0, host.getRows() - 1, host.getColumns() - 1, item);
    return this;
  }

  public InventoryContent fillRect(final int fromRow, final int fromColumn, final int toRow, final int toColumn, final ClickableItem item) {
    for (int row = fromRow; row <= toRow; row++) {
      for (int column = fromColumn; column <= toColumn; column++) {
        if (row != fromRow && row != toRow && column != fromColumn && column != toColumn) {
          continue;
        }

        set(row, column, item);
      }
    }

    return this;
  }

  public InventoryContent fillRect(final SlotPos fromPos, final SlotPos toPos, final ClickableItem item) {
    return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
  }

  public <T> T property(final String name) {
    return (T) properties.get(name);
  }

  public <T> T property(final String name, final T def) {
    return properties.containsKey(name) ? (T) properties.get(name) : def;
  }

  public Map<String, Object> properties() {
    return properties;
  }

  public InventoryContent setProperty(final String name, final Object value) {
    properties.put(name, value);
    return this;
  }

  private void update(final int row, final int column, final ClickableItem item) {
    if (item == null || item.getItem() != null) {
      return;
    }
    inventory.setItem(host.getColumns() * row + column, item.getItem());
  }

  public InventoryContent updateMeta(final SlotPos pos, final ItemMeta meta) {
    final int slot = host.getColumns() * pos.getRow() + pos.getColumn();
    ItemStack item = inventory.getItem(slot);
    Objects.requireNonNull(item);
    item.setItemMeta(meta);
    return this;
  }

  public Player getHolder() {
    return holder;
  }

  public Inventory getInventory() {
    return inventory;
  }


}
