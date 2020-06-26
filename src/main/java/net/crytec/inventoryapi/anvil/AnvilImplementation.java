package net.crytec.inventoryapi.anvil;

import java.lang.reflect.Field;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.ChatMessage;
import net.minecraft.server.v1_16_R1.Container;
import net.minecraft.server.v1_16_R1.ContainerAccess;
import net.minecraft.server.v1_16_R1.ContainerAnvil;
import net.minecraft.server.v1_16_R1.Containers;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.ItemStack;
import net.minecraft.server.v1_16_R1.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_16_R1.PacketPlayOutOpenWindow;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilImplementation {

  protected AnvilImplementation() {
  }

  public int getNextContainerId(final Player player) {
    return toNMS(player).nextContainerCounter();
  }

  public void handleInventoryCloseEvent(final Player player) {
    CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
  }

  public void sendPacketOpenWindow(final Player player, final int containerId) {
    toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, new ChatMessage("Anvil")));
  }

  public void sendPacketCloseWindow(final Player player, final int containerId) {
    toNMS(player).playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
  }

  public void setActiveContainerDefault(final Player player) {
    toNMS(player).activeContainer = toNMS(player).defaultContainer;
  }

  public void setActiveContainer(final Player player, final Object container) {
    toNMS(player).activeContainer = (Container) container;
  }

  public void setActiveContainerId(final Object container, final int containerId) {
    Field id = null;
    try {

      id = Container.class.getField("windowId");
      id.setAccessible(true);
      id.setInt(container, containerId);

    } catch (final SecurityException | NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void addActiveContainerSlotListener(final Object container, final Player player) {
    ((Container) container).addSlotListener(toNMS(player));
  }

  public Inventory toBukkitInventory(final Object container) {
    return ((Container) container).getBukkitView().getTopInventory();
  }

  public Object newContainerAnvil(final Player player) {
    return new AnvilContainer(toNMS(player));
  }

  private EntityPlayer toNMS(final Player player) {
    return ((CraftPlayer) player).getHandle();
  }

  /**
   * Modifications to ContainerAnvil that makes it so you don't have to have xp to use this anvil
   */
  private class AnvilContainer extends ContainerAnvil {

    public AnvilContainer(final EntityHuman entityhuman) {
      super(getNextContainerId((Player) entityhuman.getBukkitEntity()), entityhuman.inventory, ContainerAccess.at(entityhuman.world, new BlockPosition(0, 0, 0)));
      checkReachable = false;
      setTitle(new ChatMessage("Repair & Name"));

      final org.bukkit.inventory.ItemStack display = new org.bukkit.inventory.ItemStack(Material.COAL);

      output = CraftItemStack.asNMSCopy(display);
      getSlot(1).set(CraftItemStack.asNMSCopy(display));
    }

    private final ItemStack output;

    @Override
    public void e() {
      levelCost.set(0);
      if (renameText != null && !renameText.isEmpty()) {
        output.a(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', renameText)));
        getSlot(2).set(output);
      } else {
        return;
      }
      CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), output);
      c();
    }
  }

}
