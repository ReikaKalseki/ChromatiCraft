/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.lang.reflect.Constructor;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.FMLCommonHandler;


public enum ThreadedGenerators {

	STRUCTURE(StructureCalculator.class),
	BIOME(BiomeDistributor.class),
	REGION(RegionMapper.class),
	//SKYRIVER(SkyRiverGenerator.class),
	;

	private final Class generator;

	private boolean isRunning;

	public static final ThreadedGenerators[] generators = values();

	private static final EnumMap<ThreadedGenerators, ThreadedGenerator> runMap = new EnumMap(ThreadedGenerators.class);

	private ThreadedGenerators(Class<? extends ThreadedGenerator> c) {
		generator = c;
	}

	public boolean isDependentOn(ThreadedGenerators g) {
		switch(this) {
			case BIOME:
				return g == ThreadedGenerators.STRUCTURE;
			case REGION:
				return g == ThreadedGenerators.STRUCTURE;
			case STRUCTURE:
				return false;
				//case SKYRIVER:
				//	return false;
		}
		return false;
	}

	public void run(long seed) {
		try {
			Runnable r = new ThreadedGeneratorRunnable(this, seed);
			Thread t = new Thread(r, "ChromatiCraft Dimension "+this.getName());
			t.start();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not start threaded generator "+this.getName(), e);
		}
	}

	private ThreadedGenerator getGenerator(long seed) {
		try {
			Constructor<ThreadedGenerator> c = generator.getConstructor(long.class);
			ThreadedGenerator g = c.newInstance(seed);
			return g;
		}
		catch (Exception e) {
			throw new RuntimeException("Could not start threaded generator "+this.getName(), e);
		}
	}

	ThreadedGenerator getCurrentlyActiveGenerator() {
		return runMap.get(this);
	}

	public int getBit() {
		return (1 << this.ordinal());
	}

	private String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	private static class ThreadedGeneratorRunnable implements Runnable {

		private final ThreadedGenerators generator;
		private final ThreadedGenerator generatorObject;
		private final long seed;

		private ThreadedGeneratorRunnable(ThreadedGenerators g, long s) {
			generator = g;
			seed = s;
			generatorObject = generator.getGenerator(seed);
			runMap.put(generator, generatorObject);
		}

		public void run() {
			if (generator.isRunning) {
				String msg = "You cannot run two threaded generator instances simultaneously!";
				FMLCommonHandler.instance().raiseException(new IllegalStateException(msg), msg, true);
			}
			generator.isRunning = true;
			ChromatiCraft.logger.log("Initializing Dimension "+generator.getName()+" generation thread...");
			try {
				long time = System.currentTimeMillis();
				generatorObject.run();
				double el = (System.currentTimeMillis()-time)/1000D;
				String sg = generatorObject.getStateMessage();
				ChromatiCraft.logger.log(String.format("Dimension "+generator.getName()+" generation thread complete; Elapsed time: %.3fs. %s", el, sg));
			}
			catch (Throwable e) {
				e.printStackTrace();
				String msg = "Dimension "+generator.getName()+" generation thread failed with "+e.getClass().getName()+".";
				if (e instanceof ConcurrentModificationException)
					msg = msg+" A CME may be a transient issue. Try restarting to see if it happens consistently.";
				FMLCommonHandler.instance().raiseException(e, msg, true);
				ChromatiCraft.logger.logError(msg);
			}
			ChunkProviderChroma.finishGeneration(generator);
			generator.isRunning = false;
		}

	}

	public static void reset() {
		runMap.clear();
		ChromatiCraft.logger.log("All "+generators.length+" dimension generators complete.");
	}

}
