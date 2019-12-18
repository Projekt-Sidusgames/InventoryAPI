package net.crytec.inventoryapi.api;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.crytec.inventoryapi.SmartInventory;

public class SlotIterator {

  private final InventoryContent content;
  private final SmartInventory inventory;

  private final Type type;

  private boolean started = false;
  private boolean allowOverride = true;
  private int row;
  private int column;

  private final Set<SlotPos> blacklisted = Sets.newHashSet();

  public SlotIterator(final InventoryContent content, final SmartInventory inventory, final Type type, final int startRow, final int startColumn) {
    this.content = content;
    this.inventory = inventory;

    this.type = type;

    row = startRow;
    column = startColumn;
  }

  public SlotIterator(final InventoryContent content, final SmartInventory inventory, final Type type) {
    this(content, inventory, type, 0, 0);
  }

  public Optional<ClickableItem> get() {
    return content.get(row, column);
  }

  public SlotIterator set(final ClickableItem item) {
    if (canPlace()) {
      content.set(row, column, item);
    }

    return this;
  }

  public SlotIterator previous() {
    if (row == 0 && column == 0) {
      started = true;
      return this;
    }

    do {
      if (!started) {
        started = true;
      } else {
        switch (type) {
          case HORIZONTAL:
            column--;

            if (column == 0) {
              column = inventory.getColumns() - 1;
              row--;
            }
            break;
          case VERTICAL:
            row--;

            if (row == 0) {
              row = inventory.getRows() - 1;
              column--;
            }
            break;
        }
      }
    } while (!canPlace() && (row != 0 || column != 0));

    return this;
  }

  public SlotIterator next() {
    if (ended()) {
      started = true;
      return this;
    }

    do {
      if (!started) {
        started = true;
      } else {
        switch (type) {
          case HORIZONTAL:
            column = ++column % inventory.getColumns();

            if (column == 0) {
              row++;
            }
            break;
          case VERTICAL:
            row = ++row % inventory.getRows();

            if (row == 0) {
              column++;
            }
            break;
        }
      }
    } while (!canPlace() && !ended());

    return this;
  }


  public SlotIterator blacklist(final int row, final int column) {
    blacklisted.add(SlotPos.of(row, column));
    return this;
  }

  public SlotIterator blacklist(final SlotPos slotPos) {
    return blacklist(slotPos.getRow(), slotPos.getColumn());
  }

  public int row() {
    return row;
  }

  public SlotIterator row(final int row) {
    this.row = row;
    return this;
  }

  public int column() {
    return column;
  }

  public SlotIterator column(final int column) {
    this.column = column;
    return this;
  }

  public boolean started() {
    return started;
  }

  public boolean ended() {
    return row == inventory.getRows() - 1 && column == inventory.getColumns() - 1;
  }

  public boolean doesAllowOverride() {
    return allowOverride;
  }

  public SlotIterator allowOverride(final boolean override) {
    allowOverride = override;
    return this;
  }

  private boolean canPlace() {
    return !blacklisted.contains(SlotPos.of(row, column)) && (allowOverride || !get().isPresent());
  }


  public enum Type {
    HORIZONTAL,
    VERTICAL
  }

}
