package net.crytec.inventoryapi.anvil;

import java.util.function.BiFunction;
import net.crytec.inventoryapi.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilGUI {

  private static final AnvilImplementation WRAPPER;
  private static final AnvilListener listener;

  static {
    WRAPPER = new AnvilImplementation();
    listener = new AnvilListener();
    Bukkit.getPluginManager().registerEvents(listener, InventoryAPI.get().getHost());
  }

  private final Player holder;
  private final ItemStack insert;
  private final BiFunction<Player, String, String> biFunction;
  private final int containerId;
  private final Inventory inventory;
  private boolean open;

  /**
   * Create an AnvilGUI and open it for the player. Left slot has a model data of 2000 in 1.14+ builds. (PAPER) The right slot has a model data of 2002 in 1.14+ builds. (COAL)
   *
   * @param holder     The {@link Player} to open the inventory for
   * @param insert     What to have the text already set to
   * @param biFunction A {@link BiFunction} that is called when the player clicks the {@link Slot#OUTPUT} slot
   * @throws NullPointerException If the server version isn't supported
   */
  public AnvilGUI(final Player holder, final String insert, final BiFunction<Player, String, String> biFunction) {
    this.holder = holder;
    this.biFunction = biFunction;

    final ItemStack paper = new ItemStack(Material.PAPER);
    final ItemMeta paperMeta = paper.getItemMeta();
    paperMeta.setDisplayName(insert);
    paper.setItemMeta(paperMeta);
    this.insert = paper;

    WRAPPER.handleInventoryCloseEvent(holder);
    WRAPPER.setActiveContainerDefault(holder);

    final Object container = WRAPPER.newContainerAnvil(holder);

    inventory = WRAPPER.toBukkitInventory(container);
    inventory.setItem(Slot.INPUT_LEFT, this.insert);

    containerId = WRAPPER.getNextContainerId(holder);
    WRAPPER.sendPacketOpenWindow(holder, containerId);
    WRAPPER.setActiveContainer(holder, container);
    WRAPPER.setActiveContainerId(container, containerId);
    WRAPPER.addActiveContainerSlotListener(container, holder);

    open = true;
    listener.add(holder, this);
  }

  /**
   * Closes the inventory if it's open.
   *
   * @throws IllegalArgumentException If the inventory isn't open
   */
  public void closeInventory() {
    if (!open) {
      listener.remove(holder);
      return;
    }
    open = false;

    WRAPPER.handleInventoryCloseEvent(holder);
    WRAPPER.setActiveContainerDefault(holder);
    WRAPPER.sendPacketCloseWindow(holder, containerId);
  }


  /**
   * The player who has the GUI open
   */
  public Player getHolder() {
    return holder;
  }

  /**
   * The ItemStack that is in the {@link Slot#INPUT_LEFT} slot.
   */
  public ItemStack getInsert() {
    return insert;
  }

  /**
   * Called when the player clicks the {@link Slot#OUTPUT} slot
   */
  public BiFunction<Player, String, String> getBiFunction() {
    return biFunction;
  }

  /**
   * The inventory that is used on the Bukkit side of things
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Represents the state of the inventory being open
   */
  public boolean isOpen() {
    return open;
  }


  /**
   * The container id of the inventory, used for NMS methods
   */
  public int getContainerId() {
    return containerId;
  }


  public static class Slot {

    /**
     * The slot on the far left, where the first input is inserted. An {@link ItemStack} is always inserted here to be renamed
     */
    public static final int INPUT_LEFT = 0;
    /**
     * Not used, but in a real anvil you are able to put the second item you want to combine here
     */
    public static final int INPUT_RIGHT = 1;
    /**
     * The output slot, where an item is put when two items are combined from {@link #INPUT_LEFT} and {@link #INPUT_RIGHT} or {@link #INPUT_LEFT} is renamed
     */
    public static final int OUTPUT = 2;

  }

}
