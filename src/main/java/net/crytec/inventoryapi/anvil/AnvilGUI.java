package net.crytec.inventoryapi.anvil;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.PacketPlayOutCloseWindow;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilGUI {

  /**
   * The player who has the GUI open
   */
  private final Player player;
  private final EntityPlayer entityPlayer;
  /**
   * The title of the anvil inventory
   */
  private final String inventoryTitle;
  /**
   * The ItemStack that is in the {@link AnvilSlot#INPUT_LEFT} slot.
   */
  private ItemStack insert;
  /**
   * A state that decides where the anvil GUI is able to be closed by the user
   */
  private final boolean preventClose;
  /**
   * An {@link Consumer} that is called when the anvil GUI is closed
   */
  private final Consumer<Player> closeListener;
  /**
   * An {@link BiFunction} that is called when the {@link AnvilSlot#OUTPUT} slot has been clicked
   */
  protected final BiFunction<Player, String, Response> completeFunction;

  /**
   * The container id of the inventory, used for NMS methods
   */
  private int containerId;
  /**
   * The inventory that is used on the Bukkit side of things
   */
  private Inventory inventory;
  /**
   * Represents the state of the inventory being open
   */
  private boolean open;

  /**
   * Create an AnvilGUI and open it for the player.
   *
   * @param holder     The {@link Player} to open the inventory for
   * @param insert     What to have the text already set to
   * @param biFunction A {@link BiFunction} that is called when the player clicks the {@link AnvilSlot#OUTPUT} slot
   * @throws NullPointerException If the server version isn't supported
   * @deprecated As of version 1.2.3, use {@link AnvilGUI.Builder}
   */
  @Deprecated
  public AnvilGUI(final Player holder, final String insert, final BiFunction<Player, String, String> biFunction) {
    this(holder, "Repair & Name", insert, null, false, null, (player, text) -> {
      final String response = biFunction.apply(player, text);
      if (response != null) {
        return Response.text(response);
      } else {
        return Response.close();
      }
    });
  }

  /**
   * Create an AnvilGUI and open it for the player.
   *
   * @param player           The {@link Player} to open the inventory for
   * @param inventoryTitle   What to have the text already set to
   * @param itemText         The name of the item in the first slot of the anvilGui
   * @param insert           The material of the item in the first slot of the anvilGUI
   * @param preventClose     Whether to prevent the inventory from closing
   * @param closeListener    A {@link Consumer} when the inventory closes
   * @param completeFunction A {@link BiFunction} that is called when the player clicks the {@link AnvilSlot#OUTPUT} slot
   */
  private AnvilGUI(
      final Player player,
      final String inventoryTitle,
      final String itemText,
      final ItemStack insert,
      final boolean preventClose,
      final Consumer<Player> closeListener,
      final BiFunction<Player, String, Response> completeFunction
  ) {
    this.player = player;
    entityPlayer = ((CraftPlayer) player).getHandle();
    this.inventoryTitle = inventoryTitle;
    this.insert = insert;
    this.preventClose = preventClose;
    this.closeListener = closeListener;
    this.completeFunction = completeFunction;

    if (itemText != null) {
      if (insert == null) {
        this.insert = new ItemStack(Material.PAPER);
      }

      final ItemMeta paperMeta = this.insert.getItemMeta();
      paperMeta.setDisplayName(itemText);
      this.insert.setItemMeta(paperMeta);
    }

    openInventory();
  }

  protected boolean isOpen() {
    return open;
  }

  protected boolean isPreventClose() {
    return preventClose;
  }

  /**
   * Opens the anvil GUI
   */
  protected void openInventory() {
    CraftEventFactory.handleInventoryCloseEvent(entityPlayer);
    entityPlayer.activeContainer = entityPlayer.defaultContainer;

    final AnvilContainer container = new AnvilContainer(player, inventoryTitle);

    inventory = container.toBukkitInventory(container);
    inventory.setItem(AnvilSlot.INPUT_LEFT, insert);

    inventory.setItem(AnvilSlot.INPUT_RIGHT, new ItemStack(Material.COAL_BLOCK));

    containerId = container.getNextContainerId(player, container);

    container.sendPacketOpenWindow(player, containerId, inventoryTitle);
    container.setActiveContainer(player, container);
    container.setActiveContainerId(container, containerId);
    container.addActiveContainerSlotListener(container, player);
    open = true;
    AnvilListener.register(player, this);
  }

  /**
   * Closes the inventory if it's open.
   */
  protected void closeInventory() {
    if (!open) {
      return;
    }

    open = false;

    CraftEventFactory.handleInventoryCloseEvent(entityPlayer);
    entityPlayer.activeContainer = entityPlayer.defaultContainer;
    entityPlayer.playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));

    if (closeListener != null) {
      closeListener.accept(player);
    }
    AnvilListener.unregister(player);
  }

  /**
   * Returns the Bukkit inventory for this anvil gui
   *
   * @return the {@link Inventory} for this anvil gui
   */
  public Inventory getInventory() {
    return inventory;
  }


  public static class Builder {

    /**
     * An {@link Consumer} that is called when the anvil GUI is closed
     */
    private Consumer<Player> closeListener;
    /**
     * A state that decides where the anvil GUI is able to be closed by the user
     */
    private boolean preventClose = false;
    /**
     * An {@link BiFunction} that is called when the anvil output slot has been clicked
     */
    private BiFunction<Player, String, Response> completeFunction;
    /**
     * The text that will be displayed to the user
     */
    private String title = "Repair & Name";
    /**
     * The starting text on the item
     */
    private String itemText = "";
    /**
     * An {@link ItemStack} to be put in the input slot
     */
    private ItemStack item;

    /**
     * Prevents the closing of the anvil GUI by the user
     *
     * @return The {@link Builder} instance
     */
    public Builder preventClose() {
      preventClose = true;
      return this;
    }

    /**
     * Listens for when the inventory is closed
     *
     * @param closeListener An {@link Consumer} that is called when the anvil GUI is closed
     * @return The {@link Builder} instance
     * @throws IllegalArgumentException when the closeListener is null
     */
    public Builder onClose(final Consumer<Player> closeListener) {
      Validate.notNull(closeListener, "closeListener cannot be null");
      this.closeListener = closeListener;
      return this;
    }

    /**
     * Handles the inventory output slot when it is clicked
     *
     * @param completeFunction An {@link BiFunction} that is called when the user clicks the output slot
     * @return The {@link Builder} instance
     * @throws IllegalArgumentException when the completeFunction is null
     */
    public Builder onComplete(final BiFunction<Player, String, Response> completeFunction) {
      Validate.notNull(completeFunction, "Complete function cannot be null");
      this.completeFunction = completeFunction;
      return this;
    }

    /**
     * Sets the inital item-text that is displayed to the user
     *
     * @param text The initial name of the item in the anvil
     * @return The {@link Builder} instance
     * @throws IllegalArgumentException if the text is null
     */
    public Builder text(final String text) {
      Validate.notNull(text, "Text cannot be null");
      itemText = text;
      return this;
    }

    /**
     * Sets the AnvilGUI title that is to be displayed to the user
     *
     * @param title The title that is to be displayed to the user
     * @return The {@link Builder} instance
     * @throws IllegalArgumentException if the title is null
     */
    public Builder title(final String title) {
      Validate.notNull(title, "title cannot be null");
      this.title = title;
      return this;
    }

    /**
     * Sets the {@link ItemStack} to be put in the first slot
     *
     * @param item The {@link ItemStack} to be put in the first slot
     * @return The {@link Builder} instance
     * @throws IllegalArgumentException if the {@link ItemStack} is null
     */
    public Builder item(final ItemStack item) {
      Validate.notNull(item, "item cannot be null");
      this.item = item;
      return this;
    }

    /**
     * Creates the anvil GUI and opens it for the player
     *
     * @param player The {@link Player} the anvil GUI should open for
     * @return The {@link AnvilGUI} instance from this builder
     * @throws IllegalArgumentException when the onComplete function, plugin, or player is null
     */
    public AnvilGUI open(final Player player) {
      Validate.notNull(completeFunction, "Complete function cannot be null");
      Validate.notNull(player, "Player cannot be null");
      return new AnvilGUI(player, title, itemText, item, preventClose, closeListener, completeFunction);
    }

  }

}