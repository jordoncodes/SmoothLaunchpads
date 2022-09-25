package itzjordon.smoothlaunchpads.launchpad;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import itzjordon.smoothlaunchpads.SmoothLaunchpads;
import itzjordon.smoothlaunchpads.util.VelocityUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
				if(e.getClickedBlock().getType().toString().contains("PLATE")) {
					if (e.getClickedBlock().getLocation().subtract(0, 2, 0).getBlock().getType().toString().contains("SIGN")) {
						Block block = e.getClickedBlock().getLocation().subtract(0, 2, 0).getBlock();
						BlockState state = block.getState();
						Sign sign = (Sign) state;
						if (sign.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&8[&3LaunchPad&8]"))) {
							String coords = sign.getLine(2);
							coords = coords.replaceAll(Pattern.compile(",|[x-z]|:").pattern(), "").replaceAll("  ", " ");
							List<Double> newCoords = Arrays.stream(coords.split(Pattern.quote(" "))).filter(s -> s!=null&&!s.isEmpty()).map(Double::parseDouble).collect(Collectors.toList());
							Location l = e.getClickedBlock().getLocation().getBlock().getLocation().add(0.5, 1.25, 0.5);

//							Location tpLoc = e.getPlayer().getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
//							tpLoc.setY(e.getPlayer().getLocation().getY());
//							e.getPlayer().teleport(tpLoc);

							new BukkitRunnable() {
								@Override
								public void run() {
									Entity en = l.getWorld().spawnEntity(l, EntityType.ENDERMITE);
									NBTEntity nbte = new NBTEntity(en);
									((LivingEntity)en).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 32767, 10, true, false));
									nbte.mergeCompound(new NBTContainer("{Silent:1,Invulnerable:1,Invisible:1}"));
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
									Vector target = new Location(en.getWorld(), newCoords.get(0), newCoords.get(1), newCoords.get(2)).toVector();

									double dX,dZ;
									dX = target.getX() - en.getLocation().getX();
									dZ = target.getZ() - en.getLocation().getZ();

									double dXZ = dX+dZ;

									en.teleport(l.clone().add(0,2,0));
									final Vector veloc = new Vector(0, 0.65, 0);
									int timesDoneTotal = 1 + (int) (dXZ/20);

									en.setVelocity(veloc);
									new BukkitRunnable() {
										int timesDone = 0;
										@Override
										public void run() {
											timesDone++;
											if (en.isDead() || en.getPassenger() == null) {
												en.remove();
												cancel();
												return;
											}
											Vector pos = en.getLocation().toVector();
											double diff = target.getY()-pos.getY();
											Vector veloc2 = VelocityUtil.velocityForLaunchpad(pos, target, 1);
											en.setVelocity(en.getVelocity().add(new Vector(veloc2.getX()*0.245, en.getLocation().getY()<target.getY() ? ((target.getY()-en.getLocation().getY())/2)*0.03 : (timesDone<timesDoneTotal?0.015:0), veloc2.getZ()*0.245)));
											if (en.getLocation().distance(target.toLocation(en.getWorld())) < 2) {
												en.getPassenger().teleport(en.getPassenger().getLocation().add(0.5, 0.5, 0.5));
												en.remove();
											}

										}
									}.runTaskTimer(SmoothLaunchpads.getInstance(), 1, 1);
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
		if (e.getDismounted().getType() == EntityType.ENDERMITE) {
			e.getDismounted().setPassenger(e.getEntity());
		}
	}

//	This used to use an ender pearl:
//
//
//	@EventHandler
//	public void onEnderPearlLand(ProjectileHitEvent e) {
//		if (e.getEntity().getPassenger() != null) {
//			return;
//		}
//		if (!e.getEntity().getType().equals(EntityType.ENDERMITE)) {
//			return;
//		}
//		Entity passenger = e.getEntity().getPassenger();
//		if (passenger instanceof Player) {
//			Player player = (Player) passenger;
//			player.teleport(player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5));
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					player.teleport(player.getLocation().add(0f, 2f, 0f));
//				}
//			}.runTaskLater(plugin, 5);
//		}
//	}

//	@EventHandler
//	public void onEntityHit(EntityDamageByEntityEvent e) {
//		if (e.getDamager() instanceof Endermite) {
//			System.out.println("HIT AN ENTITY");
//			if (e.getDamager().getPassenger() != null) {
//				e.setCancelled(true);
//				Location l = e.getDamager().getLocation().add(0,2,0);
//				Entity en = l.getWorld().spawnEntity(l, EntityType.ENDERMITE);
//				if (en == null) {
//					return;
//				}
//				en.setPassenger(e.getEntity());
//			}
//		}
//	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("smoothlaunchpads.makesigns")) {
			if (e.getLine(0).equalsIgnoreCase("[LP]")) {
				e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&8[&3LaunchPad&8]"));
			}
		}
	}
	
	

}
