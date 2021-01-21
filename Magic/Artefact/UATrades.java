/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.codec.Charsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.ConnectionErrorHandler;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.PlayerSpecificTrade;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;


public class UATrades implements ConnectionErrorHandler {

	public static final int BASE_TRADE_YIELD = 48;

	public static final UATrades instance = new UATrades();

	private final HashMap<String, Integer> prices = new HashMap();
	private int MAX_PRICE = -1;

	private final ArrayList<Class<? extends UATypeTrade>> tradeList = new ArrayList();
	private final MultiMap<String, EDCommodityHook> externalHooks = new MultiMap().setNullEmpty();

	private UATrades() {

	}

	public void loadData() {
		this.loadDefaults();

		for (Class c : this.getClass().getDeclaredClasses()) {
			if (c.getSuperclass() == UATypeTrade.class) {
				tradeList.add(c);
			}
		}

		boolean jvm = ReikaJVMParser.isArgumentPresent("useCachedEDDBForCC");
		if (jvm) {
			File f = new File(DragonAPICore.getMinecraftDirectory(), "ChromatiCraft_Data/eddbcache.json");
			if (f.exists() && System.currentTimeMillis()-f.lastModified() < 24*3600*1000) { //1d
				try (BufferedReader r = ReikaFileReader.getReader(f, Charsets.UTF_8)) {
					if (r != null) {
						this.loadJSONData(r);
						return;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				f.delete();
			}
		}

		try {
			URL url = new URL("https://eddb.io/archive/v6/commodities.json"); //dynamically load the prices from eddb, so that the ones in CC can match the ones in E:D in real time, because :D
			try (BufferedReader r = ReikaFileReader.getReader(url, 2000, this, null)) {
				if (r == null) {
					throw new IOException("Could not read URL!");
				}
				else {
					this.loadJSONData(r);
					if (jvm) {
						File f = new File(DragonAPICore.getMinecraftDirectory(), "ChromatiCraft_Data/eddbcache.json");
						f.getParentFile().mkdirs();
						f.delete();
						ReikaFileReader.copyFile(url.openStream(), new FileOutputStream(f), 4096, null);
						ChromatiCraft.logger.log("Downloaded eddb.io commodity data for caching.");
					}
				}
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void registerCommodityHook(EDCommodityHook ech) {
		externalHooks.addValue(ech.getCommodityID(), ech);
	}

	private void loadJSONData(Reader r) {
		JsonReader jr = new JsonReader(r);
		jr.setLenient(true);
		JsonElement e = new JsonParser().parse(jr);
		if (e instanceof JsonArray) {
			JsonArray j = (JsonArray)e;
			Iterator<JsonElement> it = j.iterator();
			while (it.hasNext()) {
				JsonElement elem = it.next();
				if (elem instanceof JsonObject) {
					JsonObject obj = (JsonObject)elem;
					JsonElement n = obj.get("name");
					String data = n.getAsString();
					if (prices.containsKey(data)) {
						int price = obj.get("average_price").getAsInt();
						prices.put(data, price);
						ChromatiCraft.logger.log("Loading price data for "+data+" from eddb.io database: "+price+" CR");
						Collection<EDCommodityHook> c = externalHooks.get(data);
						if (c != null) {
							for (EDCommodityHook eh : c) {
								JsonElement max = obj.get("max_sell_price");
								JsonElement upper = obj.get("sell_price_upper_average");
								eh.onPriceReceived(price, upper != null ? upper.getAsInt() : price, max != null ? max.getAsInt() : price);
							}
						}
					}
				}
			}
		}
	}

	private void loadDefaults() {
		this.addPrice("Thargoid Sensor", 288000); //sells for ~288kCr anywhere, though a bit more outside the pleiades
		this.addPrice("Meta-Alloys", 114000);  //sells for ~114kCr in the bubble
		for (EDCommodityHook eh : externalHooks.allValues(false)) {
			this.addPrice(eh.getCommodityID(), 0);
		}
	}

	private void addPrice(String id, int val) {
		prices.put(id, val);
		MAX_PRICE = Math.max(MAX_PRICE, val);
	}

	public void addTradesToVillager(EntityVillager ev, MerchantRecipeList li, Random rand) {
		for (Class<? extends UATypeTrade> trade : tradeList) {
			if (!this.hasTrade(ev.buyingList, trade)) {
				try {
					ev.buyingList.add(trade.newInstance()); //add an unlocked trade
				}
				catch (Exception e) {
					ChromatiCraft.logger.logError("Could not add trade type "+trade+" to villager!");
					e.printStackTrace();
				}
			}
		}
		for (EDCommodityHook eh : externalHooks.allValues(false)) {
			MerchantRecipe trade = eh.createTrade();
			if (!this.hasTrade(ev.buyingList, trade)) {
				try {
					ev.buyingList.add(trade);
				}
				catch (Exception e) {
					ChromatiCraft.logger.logError("Could not add delegated trade "+trade+" to villager!");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean hasTrade(MerchantRecipeList li, MerchantRecipe tr) {
		for (Object r : li) {
			if (r.getClass() == tr.getClass())
				return true;
		}
		return false;
	}

	private boolean hasTrade(MerchantRecipeList li, Class<? extends UATypeTrade> tr) {
		for (Object r : li) {
			if (r.getClass() == tr)
				return true;
		}
		return false;
	}

	private static int calcValue(String id) {
		int price = instance.prices.get(id);
		return price*BASE_TRADE_YIELD/instance.MAX_PRICE;
	}

	public static abstract class UATypeTrade extends MerchantRecipe implements PlayerSpecificTrade {

		public final int emeraldValue;

		protected UATypeTrade(ItemStack in, String priceID) {
			super(in, new ItemStack(Items.emerald, calcValue(priceID), 0));
			emeraldValue = calcValue(priceID);
		}

		@Override
		public final void incrementToolUses() {
			//No-op to prevent expiry
		}

	}

	public static class UATrade extends UATypeTrade {

		UATrade() {
			super(ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal()), "Thargoid Sensor");
		}

		@Override
		public boolean isValid(EntityPlayer ep) {
			return ProgressStage.ARTEFACT.isPlayerAtStage(ep);
		}

	}

	public static class MetaAlloyTrade extends UATypeTrade {

		MetaAlloyTrade() {
			super(ChromaBlocks.METAALLOYLAMP.getStackOf(), "Meta-Alloys");
		}

		@Override
		public boolean isValid(EntityPlayer ep) {
			return true;
		}

	}
	/*
	public static class VoidOpalTrade extends UATypeTrade {

		VoidOpalTrade() {
			super(ChromaStacks.voidOpal, "Void Opals");
		}

		@Override
		public boolean isValid(EntityPlayer ep) {
			return true;
		}

	}*/

	@Override
	public void onServerRedirected() {
		ChromatiCraft.logger.logError("Could not load commodity price data - server redirected!");
	}

	@Override
	public void onTimedOut() {
		ChromatiCraft.logger.logError("Could not load commodity price data - timed out!");
	}

	@Override
	public void onNoInternet() {
		ChromatiCraft.logger.logError("Could not load commodity price data - no internet connection!");
	}

	@Override
	public void onServerNotFound() {
		ChromatiCraft.logger.logError("Could not load commodity price data - server not found!");
	}

	public static interface EDCommodityHook {

		String getCommodityID();
		MerchantRecipe createTrade();
		void onPriceReceived(int average, int upper, int max);

	}

}
