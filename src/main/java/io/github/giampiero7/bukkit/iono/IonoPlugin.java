package io.github.giampiero7.bukkit.iono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IonoPlugin extends JavaPlugin implements Listener {

	private static Logger log = Logger.getLogger("Minecraft");
	private String apiUrlPrefix;

	/**
	 * 
	 */
	public void onLoad() {
		log.info("[IonoPlugin] loaded");
	}

	/**
	 * 
	 */
	public void onEnable() {
		try {
			Properties p = new Properties();
			try (BufferedReader br = Files.newBufferedReader(Paths
					.get("iono.ini"))) {
				p.load(br);
			}
			String ip = p.getProperty("ip", "192.168.0.100");
			apiUrlPrefix = "http://" + ip + "/api/";
			getServer().getPluginManager().registerEvents(this, this);
			log.info("[IonoPlugin] enabled - iono IP: " + ip);

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onEnable: " + e);
		}
	}

	/**
	 * 
	 */
	public void onDisable() {
		log.info("[IonoPlugin] disabled");
	}

	/**
	 * 
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		try {
			log.info("[IonoPlugin] command: " + cmd.getName());
			if (cmd.getName().equalsIgnoreCase("relay")) {
				if (args.length == 2) {
					String num = args[0];
					String val = args[1].equalsIgnoreCase("on") ? "1" : "0";
					String apiCommand = "set?DO" + num + "=" + val;
					try {
						sendApiCommand(apiCommand);
					} catch (Exception e) {
						log.warning("[IonoPlugin] Exception sendApiCommand: "
								+ e);
					}
					return true;
				}
			}

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onCommand: " + e);
		}

		return false;
	}

	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		try {
			log.info("[IonoPlugin] BlockPlaceEvent");

			Block b = event.getBlock();
			if (b.getType() == Material.TORCH) {
				Block d = b.getRelative(BlockFace.DOWN);
				if (d.getType() == Material.WOOL) {

					@SuppressWarnings("deprecation")
					byte data = d.getData();
					int relay;
					switch (data) {
					case 0: // white
						relay = 2;
						break;
						
					case 5: // lime
						relay = 3;
						break;
						
					case 14: // red
						relay = 1;
						break;

					default:
						return;
					}
					
					sendApiCommand("set?DO" + relay + "=1");
				}
			}

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onBlockPlaceEvent: " + e);
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		try {
			log.info("[IonoPlugin] BlockBreakEvent");

			Block b = event.getBlock();
			if (b.getType() == Material.TORCH) {
				Block d = b.getRelative(BlockFace.DOWN);
				if (d.getType() == Material.WOOL) {

					@SuppressWarnings("deprecation")
					byte data = d.getData();
					int relay;
					switch (data) {
					case 0: // white
						relay = 2;
						break;
						
					case 5: // lime
						relay = 3;
						break;
						
					case 14: // red
						relay = 1;
						break;

					default:
						return;
					}
					
					sendApiCommand("set?DO" + relay + "=0");
				}
			}

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onBlockBreakEvent: " + e);
		}
	}

	@EventHandler
	public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
		try {
			log.info("[IonoPlugin] BlockRedstoneEvent");

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onBlockRedstoneEvent: " + e);
		}
	}

	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		try {
			log.info("[IonoPlugin] BlockIgniteEvent");

		} catch (Exception e) {
			log.warning("[IonoPlugin] Exception onBlockIgniteEvent: " + e);
		}
	}

	/**
	 * 
	 * @param cmd
	 * @return
	 * @throws IOException
	 */
	private String sendApiCommand(String cmd) throws IOException {
		URLConnection connection = (new URL(apiUrlPrefix + cmd))
				.openConnection();
		connection.setConnectTimeout(5000);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()))) {
			String line;
			StringBuilder resp = new StringBuilder();
			while ((line = in.readLine()) != null) {
				resp.append(line);
				resp.append("\n");
			}

			return resp.toString();
		}
	}
}
