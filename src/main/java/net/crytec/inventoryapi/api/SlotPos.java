package net.crytec.inventoryapi.api;

import java.util.Objects;

public class SlotPos {

  private final int row;
  private final int column;

  public SlotPos(final int row, final int column) {
    this.row = row;
    this.column = column;
  }

  public int getRow() {
    return row;
  }

  @Override
  public int hashCode() {
    return Objects.hash(column, row);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SlotPos)) {
      return false;
    }
    final SlotPos other = (SlotPos) obj;
    return column == other.column && row == other.row;
  }

  public int getColumn() {
    return column;
  }

  public static SlotPos of(final int row, final int column) {
    return new SlotPos(row, column);
  }

  public static SlotPos of(final int slot) {
    final String radixSlot = Integer.toString(slot, 9);
    final int row = radixSlot.length() == 1 ? 0 : Integer.valueOf("" + radixSlot.charAt(0));
    final int column = Integer.valueOf("" + radixSlot.charAt(radixSlot.length() == 1 ? 0 : 1));
    return new SlotPos(row, column);
  }

}
