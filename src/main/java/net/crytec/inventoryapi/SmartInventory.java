package net.crytec.inventoryapi;

import java.util.Optional;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.opener.InventoryOpener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SmartInventory {

  private String id;
  private String title;
  private InventoryType type;
  private int rows, columns;

  private SmartInventory parent;
  private InventoryProvider provider;

  private SmartInventory() {
  }

  public Inventory open(final Player player) {
    return open(player, 0);
  }

  public Inventory open(final Player player, final int page) {
    final InventoryManager manager = InventoryManager.get();
    final Optional<SmartInventory> oldInv = manager.getInventory(player);

    oldInv.ifPresent(inv -> {
      manager.setInventory(player, null);
    });

    provider.preInit(player);

    final InventoryContent content = new InventoryContent(this, player);
    content.pagination().page(page);
    manager.setContents(player, content);

    provider.init(player, content);

    final InventoryOpener opener = manager.findOpener(type).orElseThrow(() -> new IllegalStateException("No opener found for inventory type " + type.name()));
    final Inventory handle = opener.open(this, player);
    manager.setInventory(player, this);

    return handle;
  }

  public void close(final Player player) {
    final InventoryManager manager = InventoryManager.get();
    manager.setInventory(player, null);
    player.closeInventory();
    manager.setContents(player, null);
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public InventoryType getType() {
    return type;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public InventoryProvider getProvider() {
    return provider;
  }

  public Optional<SmartInventory> getParent() {
    return Optional.ofNullable(parent);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String id = "unknown";
    private String title = "unknown";
    private InventoryType type = InventoryType.CHEST;
    private int rows = 6, columns = 9;

    private InventoryProvider provider;
    private SmartInventory parent;

    private Builder() {
    }

    public Builder id(final String id) {
      this.id = id;
      return this;
    }

    public Builder title(final String title) {
      this.title = title;
      return this;
    }

    public Builder type(final InventoryType type) {
      this.type = type;
      return this;
    }

    public Builder size(final int rows) {
      this.rows = rows;
      columns = 9;
      return this;
    }

    public Builder size(final int rows, final int columns) {
      this.rows = rows;
      this.columns = columns;
      return this;
    }

    public Builder provider(final InventoryProvider provider) {
      this.provider = provider;
      return this;
    }

    public Builder parent(final SmartInventory parent) {
      this.parent = parent;
      return this;
    }

    public SmartInventory build() {
      if (provider == null) {
        throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");
      }

      final SmartInventory inv = new SmartInventory();
      inv.id = id;
      inv.title = title;
      inv.type = type;
      inv.rows = rows;
      inv.columns = columns;
      inv.provider = provider;
      inv.parent = parent;
      return inv;
    }
  }
}