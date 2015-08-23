/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.Random;

import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.FMLCommonHandler;

public class StructureCalculator implements Runnable {

	private final Random rand = new Random();
	//private final Random seededRand;
	private final int maxAttempts;
	private final Thread callerThread;

	StructureCalculator() {
		this(10);
	}

	StructureCalculator(int max) {
		maxAttempts = max;
		callerThread = Thread.currentThread();

		//Would base off world seed, but is loaded "outside" a vMC world and as such cannot reference it; make it PC-specific instead
		//seededRand = new Random(new File("c:").getTotalSpace() ^ System.getProperty("os.name").hashCode());
	}

	@Override
	public void run() {
		ChromatiCraft.logger.log("Initializing dimension structure generation thread...");
		try {
			long time = System.nanoTime();
			this.generate();
			double el = (System.nanoTime()-time)/(10e9);
			int n = ChunkProviderChroma.structures.size();
			ChromatiCraft.logger.log(String.format("Dimension structure generation thread complete; %d structures generated. Elapsed time: %.9fs", n, el));
		}
		catch (Throwable e) {
			e.printStackTrace();
			String msg = "Dimension structure generation thread failed with "+e.getClass().getName()+".";
			FMLCommonHandler.instance().raiseException(e, msg, true);
			ChromatiCraft.logger.log(msg);
		}
		ChunkProviderChroma.finishStructureGen();
	}

	private void generate() throws OutOfMemoryError {
		//ArrayList<CrystalElement> colors = ReikaJavaLibrary.makeListFromArray(CrystalElement.elements);
		for (int i = 0; i < DimensionStructureType.types.length; i++) {
			//int index = rand.nextInt(colors.size());
			//CrystalElement e = colors.get(index);
			//colors.remove(index);
			ChunkProviderChroma.structures.add(DimensionStructureType.types[i].getGenerator());
		}

		int structureOriginX = ReikaRandomHelper.getRandomPlusMinus(0, 10000);
		int structureOriginZ = ReikaRandomHelper.getRandomPlusMinus(0, 10000);
		float structureAngleOrigin = rand.nextFloat()*360;

		for (DimensionStructureGenerator s : ChunkProviderChroma.structures) {
			this.tryGenerate(s, structureOriginX, structureOriginZ, structureAngleOrigin, 0);
		}
	}

	private void tryGenerate(DimensionStructureGenerator p, int structureOriginX, int structureOriginZ, float structureAngleOrigin, int attempt) {
		try {
			this.doGenerate(p, structureOriginX, structureOriginZ, structureAngleOrigin);
		}
		catch (Throwable e) {
			if (e instanceof OutOfMemoryError)
				throw (OutOfMemoryError)e;
			boolean redo = attempt < maxAttempts;
			StackTraceElement[] st = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("Error calculating structure "+p.getType()+": ");
			sb.append(e.toString());
			int n = Math.min(6, st.length);
			for (int i = 0; i < n; i++) {
				sb.append(" @ ");
				sb.append(st[i]);
			}
			sb.append("! ");
			if (redo)
				sb.append("Re-attempting...");
			else
				sb.append("Already failed too many ("+maxAttempts+") times. Giving up.");
			ChromatiCraft.logger.logError(sb.toString());
			p.clear();
			ChunkProviderChroma.structures.remove(p);
			if (redo)
				this.tryGenerate(p, structureOriginX, structureOriginZ, structureAngleOrigin, attempt+1);
		}
	}

	private void doGenerate(DimensionStructureGenerator s, int structureOriginX, int structureOriginZ, float structureAngleOrigin) {
		float ang = structureAngleOrigin+s.color.ordinal()*22.5F;
		int r = ReikaRandomHelper.getRandomPlusMinus(5000, 4000);
		int x = structureOriginX+(int)(r*MathHelper.cos(ang));
		int z = structureOriginZ+(int)(r*MathHelper.sin(ang));
		s.startCalculate(x, z, rand);
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			String sg = "CHROMATICRAFT: Generated a "/*+s.color+" "*/+s.getType()+" at "+s.getCentralBlockCoords();
			ReikaJavaLibrary.pConsole(sg);
		}
	}

}
