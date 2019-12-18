package net.crytec.inventoryapi;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.opener.ChestInventoryOpener;
import net.crytec.inventoryapi.api.opener.InventoryOpener;
import net.crytec.inventoryapi.api.opener.SpecialInventoryOpener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class InventoryManager {

  private static InventoryManager instance;

  private final Map<Player, SmartInventory> inventories;
  private final Map<Player, InventoryContent> contents;

  private final ChestInventoryOpener chestOpener;
  private final SpecialInventoryOpener otherOpener;

  protected InventoryManager() {
    InventoryManager.instance = this;

    inventories = Maps.newHashMap();
    contents = Maps.newHashMap();

    chestOpener = new ChestInventoryOpener();
    otherOpener = new SpecialInventoryOpener();
  }

  public static InventoryManager get() {
    Preconditions.checkNotNull(instance, "Unable to retrieve InventoryManager instance - Variable not initialized");
    return instance;
  }

  public Optional<InventoryOpener> findOpener(final InventoryType type) {
    if (type == InventoryType.CHEST && chestOpener.supports(type)) {
      return Optional.of(chestOpener);
    } else if (otherOpener.supports(type)) {
      return Optional.of(otherOpener);
    } else {
      return Optional.empty();
    }
  }

  public List<Player> getOpenedPlayers(final SmartInventory inv) {
    final List<Player> list = Lists.newArrayList();

    inventories.forEach((player, playerInv) -> {
      if (inv.equals(playerInv)) {
        list.add(player);
      }
    });

    return list;
  }

  public Optional<SmartInventory> getInventory(final Player p) {
    return Optional.ofNullable(inventories.get(p));
  }

  protected void setInventory(final Player p, final SmartInventory inv) {
    if (inv == null) {
      inventories.remove(p);
    } else {
      inventories.put(p, inv);
    }
  }

  public Optional<InventoryContent> getContents(final Player p) {
    return Optional.ofNullable(contents.get(p));
  }

  protected void setContents(final Player p, final InventoryContent contents) {
    if (contents == null) {
      this.contents.remove(p);
    } else {
      this.contents.put(p, contents);
    }
  }

  public Map<Player, SmartInventory> getInventories() {
    return inventories;
  }

  public Map<Player, InventoryContent> getContents() {
    return contents;
  }
}