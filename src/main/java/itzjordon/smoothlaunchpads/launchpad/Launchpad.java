package itzjordon.smoothlaunchpads.launchpad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import itzjordon.smoothlaunchpads.SmoothLaunchpads;
import itzjordon.smoothlaunchpads.util.VelocityUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import net.md_5.bungee.api.ChatColor;

public class Launchpad implements Listener {
	SmoothLaunchpads plugin = SmoothLaunchpads.getInstance();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onStepOnGoldPressurePlate(PlayerInteractEvent e) {
		try {
			if(e.getAction().equals(Action.PHYSICAL)) {
				e.getPlayer().teleport(e.getPlayer());
				if(e.getClickedBlock().getType().toString().contains("PLATE")) {
					if (e.getClickedBlock().getLocation().subtract(0, 2, 0).getBlock().getType().toString().contains("SIGN")) {
						Block block = e.getClickedBlock().getLocation().subtract(0, 2, 0).getBlock();
						BlockState state = block.getState();
						Sign sign = (Sign) state;
						if (sign.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&8[&3LaunchPad&8]"))) {
							String coords = sign.getLine(2);
							coords = coords.replaceAll(Pattern.compile(",|[x-z]|:").pattern(), "").replaceAll("  ", " ");
							List<Double> newCoords = Arrays.stream(coords.split(Pattern.quote(" "))).filter(s -> s!=null&&!s.isEmpty()).map(Double::parseDouble).collect(Collectors.toList());
							Vector target = new Location(e.getPlayer().getWorld(), newCoords.get(0), newCoords.get(1), newCoords.get(2)).toVector();
							Vector pos = e.getClickedBlock().getLocation().add(0, 1, 0).toVector();
							Vector velocity = VelocityUtil.velocityForLaunchpad(pos, target, 1);
							Location l = e.getClickedBlock().getLocation().add(0, 1.25, 0);
							Location toTp = l.clone().getBlock().getLocation().add(0.5, 2, 0.5);
							toTp.setPitch(e.getPlayer().getLocation().getPitch());
							toTp.setYaw(e.getPlayer().getLocation().getYaw());
							e.getPlayer().teleport(toTp);
							new BukkitRunnable() {
								@Override
								public void run() {
									Entity en = l.getWorld().spawnEntity(l, EntityType.SNOWBALL);
									if (en == null) {
										return;
									}
									try {
										e.getPlayer().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 100, 1);
									}catch (Error|Exception err) {
										try {
											e.getPlayer().playSound(l, Sound.valueOf("EXPLODE"), 100, 1);
										}catch (Error|Exception ignored){};
									}
									en.setPassenger(e.getPlayer());
									en.teleport(l.clone().add(0,2,0));
									Vector veloc = velocity.clone();
									en.setVelocity(velocity.multiply(0.65).setY(veloc.getY()));
								}
							}.runTaskLater(plugin, 5);
						}
						
					}
					
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@EventHandler
	public void onDismount(EntityDismountEvent e) {
		System.out.println(e.getDismounted().getType().toString());
		if (e.getDismounted().getType() == EntityType.SNOWBALL) {
			e.getDismounted().addPassenger(e.getEntity());
		}
	}


	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType().equals(EntityType.SNOWBALL) && !e.getDamager().getPassengers().isEmpty()) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onEnderPearlLand(ProjectileHitEvent e) {
		if (e.getEntity().getPassenger() != null || !e.getEntity().getType().equals(EntityType.SNOWBALL)) {
			return;
		}
		Entity passenger = e.getEntity().getPassenger();
		if (passenger instanceof Player) {
			Player player = (Player) passenger;
			player.teleport(player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5));
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(player.getLocation().add(0f, 2f, 0f));
				}
			}.runTaskLater(plugin, 5);
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("smoothlaunchpads.makesigns")) {
			if (e.getLine(0).equalsIgnoreCase("[LP]")) {
				e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&8[&3LaunchPad&8]"));
			}
		}
	}
	
	

}
