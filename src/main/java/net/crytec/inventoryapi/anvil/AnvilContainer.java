package net.crytec.inventoryapi.anvil;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Container;
import net.minecraft.server.v1_16_R3.ContainerAccess;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilContainer extends ContainerAnvil {

  public AnvilContainer(final Player player, final String guiTitle) {
    super(((CraftPlayer) player).getHandle().nextContainerCounter(), ((CraftPlayer) player).getHandle().inventory,
        ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
    checkReachable = false;
    setTitle(new ChatMessage(guiTitle));
  }

  @Override
  public void e() {
    super.e();
    levelCost.set(0);
  }


  public int getContainerId() {
    return windowId;
  }

  public int getNextContainerId(final Player player, final Object container) {
    return ((AnvilContainer) container).getContainerId();
  }

  public void sendPacketOpenWindow(final Player player, final int containerId, final String guiTitle) {
    toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, new ChatMessage(guiTitle)));
  }

  public void sendPacketCloseWindow(final Player player, final int containerId) {
    toNMS(player).playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
  }

  public void setActiveContainer(final Player player, final Object container) {
    toNMS(player).activeContainer = (Container) container;
  }

  public void addActiveContainerSlotListener(final Object container, final Player player) {
    ((Container) container).addSlotListener(toNMS(player));
  }

  public void setActiveContainerId(final Object container, final int containerId) {
    //noop
  }

  public Inventory toBukkitInventory(final Object container) {
    return ((Container) container).getBukkitView().getTopInventory();
  }

  private EntityPlayer toNMS(final Player player) {
    return ((CraftPlayer) player).getHandle();
  }

}

