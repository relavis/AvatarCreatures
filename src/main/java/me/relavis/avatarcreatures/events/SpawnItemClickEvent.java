package me.relavis.avatarcreatures.events;

import me.relavis.avatarcreatures.AvatarCreatures;
import me.relavis.avatarcreatures.util.DataHandler;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.PathfinderGoalSelector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SpawnItemClickEvent implements Listener {

    DataHandler data = new DataHandler();
    AvatarCreatures plugin = AvatarCreatures.getPlugin(AvatarCreatures.class);
    String appaMaterial = plugin.getConfig().getString("appa.spawn-item");
    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        UUID playerUUID = player.getUniqueId();

        if (action == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(appaMaterial)) && player.hasPermission("avatarcreatures.appa.spawn")) {
                if (player.hasPermission("avatarcreatures.appa.spawn")) {
                    createEntity(event, playerUUID, "RAVAGER");
                    event.setCancelled(true);
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission to spawn an Appa.");
                }
            }
        }
    }

    public void createEntity(PlayerInteractEvent event, UUID playerUUID, String type) {
        Player player = event.getPlayer();
        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        boolean isAlive = data.isAlive(playerUUID, type);

        if (isAlive) { // Check if player's mount entity is already spawned in
            player.sendMessage(ChatColor.RED + "You already have an Appa! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
        } else { // If it isn't:
            if (data.entityExists(playerUUID, type)) { // Check if the user has already had a mount entity. If so:
                player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            } else {
                player.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Ravager.class, entity -> {
                    entity.setRemoveWhenFarAway(false);
                    entity.setGravity(false);
                    String playerName = player.getName();
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(playerName + "'s Appa");
                    player.getInventory().setItemInMainHand(null);
                    UUID entityUUID = entity.getUniqueId();
                    data.addEntityToData(playerName, playerUUID, entityUUID, type);

                    EntityInsentient entityInsentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
                    entityInsentient.goalSelector = new PathfinderGoalSelector(entityInsentient.getWorld().getMethodProfiler());
                    entityInsentient.targetSelector = new PathfinderGoalSelector(entityInsentient.getWorld().getMethodProfiler());

                    /*


                    EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) entity).getHandle();
                    PathfinderGoalSelector goalSelector = nmsEntity.goalSelector;
                    PathfinderGoalSelector targetSelector = nmsEntity.targetSelector;

                    try {

                        Field brField = EntityLiving.class.getDeclaredField("bo");
                        brField.setAccessible(true);
                        BehaviorController<?> controller = (BehaviorController<?>) brField.get(nmsEntity);


                        Field memoriesField = BehaviorController.class.getDeclaredField("memories");
                        memoriesField.setAccessible(true);
                        memoriesField.set(controller, new HashMap<>());


                        Field sensorsField = BehaviorController.class.getDeclaredField("sensors");
                        sensorsField.setAccessible(true);
                        sensorsField.set(controller, new LinkedHashMap<>());

                        Field cField = BehaviorController.class.getDeclaredField("c");
                        cField.setAccessible(true);
                        cField.set(controller, new TreeMap<>());
                    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }


                    try {

                        Field dField;
                        dField = PathfinderGoalSelector.class.getDeclaredField("d");
                        dField.setAccessible(true);
                        dField.set(goalSelector, new LinkedHashSet<>());
                        dField.set(targetSelector, new LinkedHashSet<>());

                        Field cField;
                        cField = PathfinderGoalSelector.class.getDeclaredField("c");
                        cField.setAccessible(true);
                        dField.set(goalSelector, new LinkedHashSet<>());
                        cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

                        Field fField;
                        fField = PathfinderGoalSelector.class.getDeclaredField("f");
                        fField.setAccessible(true);
                        dField.set(goalSelector, new LinkedHashSet<>());
                        fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));

                    } catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                     */

                });
            }
        }
    }

}
