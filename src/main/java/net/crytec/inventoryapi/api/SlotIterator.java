package net.crytec.inventoryapi.api;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntSupplier;
import net.crytec.inventoryapi.SmartInventory;
import org.apache.commons.lang.mutable.MutableInt;

public class SlotIterator {

  private final InventoryContent content;
  private final SmartInventory inventory;

  private final Type type;

  private boolean started = false;
  private boolean allowOverride = true;
  private MutableInt row;
  private MutableInt column;


  private final Set<SlotPos> blacklisted = new HashSet<>();

  public SlotIterator(final InventoryContent content, final SmartInventory inventory, final Type type, final int startRow,
      final int startColumn) {
    this.content = content;
    this.inventory = inventory;

    this.type = type;

    row = new MutableInt(startRow);
    column = new MutableInt(startColumn);
  }

  public SlotIterator(final InventoryContent content, final SmartInventory inventory, final Type type) {
    this(content, inventory, type, 0, 0);
  }

  public Optional<ClickableItem> get() {
    return content.get(row.intValue(), column.intValue());
  }

  public void set(final ClickableItem item) {
    if (canPlace()) {
      content.set(row.intValue(), column.intValue(), item);
    }
  }

  public void addPagination(final Pagination pagination) {
    for (final ClickableItem item : pagination.getPageItems()) {
      next().set(item);

      if (ended()) {
        break;
      }
    }
  }

  /**
   * Dont ask me. I dont know either...
   * @return this instance for whatever reason
   */
  public SlotIterator previous() {
    if (row.intValue() == 0 && column.intValue() == 0) {
      started = true;
      return this;
    }

    MutableInt indicator = type == Type.HORIZONTAL ? this.column : this.row;
    MutableInt factor = type == Type.VERTICAL ? this.column : this.row;
    IntSupplier provider = type == Type.HORIZONTAL ? () -> inventory.getColumns() - 1 : () -> inventory.getRows() - 1;

    do {
      if (!started) {
        started = true;
      } else {
        indicator.decrement();
        if (indicator.intValue() == 0) {
          indicator.setValue(provider.getAsInt());
          factor.decrement();
        }
      }
    } while (!canPlace() && (row.intValue() != 0 || column.intValue() != 0));

    return this;
  }

  /**
   * Dont ask me. I dont know either...
   * @return this instance for whatever reason
   */
  public SlotIterator next() {
    if (ended()) {
      started = true;
      return this;
    }

    MutableInt indicator = type == Type.HORIZONTAL ? this.column : this.row;
    MutableInt factor = type == Type.VERTICAL ? this.column : this.row;
    IntSupplier provider = type == Type.HORIZONTAL ? () -> inventory.getColumns() - 1 : () -> inventory.getRows() - 1;

    do {
      if (!started) {
        started = true;
      } else {
        indicator.increment();
        indicator.setValue(indicator.intValue() % provider.getAsInt());
        if (indicator.intValue() == 0) {
          factor.increment();
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
    return row.intValue();
  }

  public SlotIterator row(final int row) {
    this.row = new MutableInt(row);
    return this;
  }

  public int column() {
    return column.intValue();
  }

  public SlotIterator column(final int column) {
    this.column = new MutableInt(column);
    return this;
  }

  public boolean started() {
    return started;
  }

  public boolean ended() {
    return row.intValue() == inventory.getRows() - 1 && column.intValue() == inventory.getColumns() - 1;
  }

  public boolean doesAllowOverride() {
    return allowOverride;
  }

  public SlotIterator allowOverride(final boolean override) {
    allowOverride = override;
    return this;
  }

  private boolean canPlace() {
    return !blacklisted.contains(SlotPos.of(row.intValue(), column.intValue())) && (allowOverride || get().isEmpty());
  }


  public enum Type {
    HORIZONTAL,
    VERTICAL
  }

}
