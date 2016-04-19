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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class StructureCalculator extends ThreadedGenerator {

	private final Random rand;
	private final Random seededRand;
	private final int maxAttempts;
	private final Thread callerThread;

	public StructureCalculator(long seed) {
		this(seed, 10);
	}

	private StructureCalculator(long seed, int max) {
		maxAttempts = max;
		rand = new Random(seed);
		callerThread = Thread.currentThread();

		//Would base off world seed, but is loaded "outside" a MC world and as such cannot reference it; make it file-specific instead
		//seededRand = new Random(new File("c:").getTotalSpace() ^ System.getProperty("os.name").hashCode());
		seededRand = new Random(this.generateOrGetGenSeed());
	}

	private long generateOrGetGenSeed() {
		File f = new File(DragonAPICore.getMinecraftDirectoryString()+"/ChromatiCraft_Data/DimensionGen.dat");
		try {
			if (f.exists()) {
				try {
					ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
					if (!li.isEmpty()) {
						String s = li.get(0);
						s = s.substring(s.indexOf(':')+1);
						return Long.parseLong(s);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					f.delete();
				}
			}
			f.getParentFile().mkdirs();
			f.createNewFile();
			long seed = System.currentTimeMillis();
			ReikaFileReader.writeLinesToFile(f, ReikaJavaLibrary.makeListFrom("Seed:"+String.valueOf(seed)), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return new File("c:").getTotalSpace() ^ System.getProperty("os.name").hashCode(); //fallback
	}

	@Override
	public void run() throws Throwable {
		this.generate();
		if ((DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || DragonAPICore.debugtest) {
			ChromatiCraft.logger.log("Generated Structures: "+ChunkProviderChroma.structures);
		}
	}

	private void generate() throws OutOfMemoryError {
		/*
		ArrayList<CrystalElement> colors = ReikaJavaLibrary.makeListFromArray(CrystalElement.elements);
		for (int i = 0; i < DimensionStructureType.types.length; i++) {
			int index = rand.nextInt(colors.size());
			CrystalElement e = colors.get(index);
			colors.remove(index);
			ChunkProviderChroma.structures.add(new StructurePair(DimensionStructureType.types[i], e));
		}
		 */

		ArrayList<DimensionStructureType> structs = this.getUsableStructures();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			int index = seededRand.nextInt(structs.size());
			DimensionStructureType e = structs.get(index);
			structs.remove(index);
			ChunkProviderChroma.structures.add(new StructurePair(e.createGenerator(), CrystalElement.elements[i]));

			if (structs.isEmpty()) {
				structs = this.getUsableStructures(); //reuse structure types as necessary
			}
		}

		int structureOriginX = ReikaRandomHelper.getRandomPlusMinus(0, 6000); //was 10K, then 4K
		int structureOriginZ = ReikaRandomHelper.getRandomPlusMinus(0, 6000);
		float structureAngleOrigin = rand.nextFloat()*360;

		for (StructurePair s : new ArrayList<StructurePair>(ChunkProviderChroma.structures)) {
			this.tryGenerate(s, structureOriginX, structureOriginZ, structureAngleOrigin, 0);
		}

		this.generateMonument(structureOriginX, structureOriginZ);
	}

	@Override
	public String getStateMessage() {
		return ChunkProviderChroma.getStructures().size()+" structures generated.";
	}

	private void generateMonument(int structureOriginX, int structureOriginZ) {
		ChunkProviderChroma.monument.startCalculate(structureOriginX, structureOriginZ, rand);
		if ((DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || DragonAPICore.debugtest) {
			String sg = "CHROMATICRAFT: Generated the monument at "+structureOriginX+", "+structureOriginZ+".";
			ReikaJavaLibrary.pConsole(sg);
		}
	}

	private ArrayList<DimensionStructureType> getUsableStructures() {
		ArrayList<DimensionStructureType> li = ReikaJavaLibrary.makeListFromArray(DimensionStructureType.types);
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			return li;
		Iterator<DimensionStructureType> it = li.iterator();
		while (it.hasNext()) {
			DimensionStructureType d = it.next();
			if (!d.isComplete()) {
				it.remove();
			}
		}
		return li;
	}

	private void tryGenerate(StructurePair p, int structureOriginX, int structureOriginZ, float structureAngleOrigin, int attempt) {
		try {
			this.doGenerate(p, structureOriginX, structureOriginZ, structureAngleOrigin);
		}
		catch (Throwable e) {
			if (e instanceof OutOfMemoryError)
				throw (OutOfMemoryError)e;
			boolean redo = attempt < maxAttempts;
			StackTraceElement[] st = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("Error calculating structure "+p.generator+": ");
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
			p.generator.clear();
			ChunkProviderChroma.structures.remove(p);
			if (redo)
				this.tryGenerate(p, structureOriginX, structureOriginZ, structureAngleOrigin, attempt+1);
		}
	}

	private void doGenerate(StructurePair s, int structureOriginX, int structureOriginZ, float structureAngleOrigin) {
		double ang = Math.toRadians(structureAngleOrigin+s.color.ordinal()*22.5);
		int r = ReikaRandomHelper.getRandomPlusMinus(5000, 3000); //was +/- 4000, then 2000
		int x = structureOriginX+(int)(r*Math.cos(ang));
		int z = structureOriginZ+(int)(r*Math.sin(ang));
		s.generator.startCalculate(s.color, x, z, rand);
		if ((DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || DragonAPICore.debugtest) {
			String sg = "CHROMATICRAFT: Generated a "+s.color+" "+s.generator+" at "+s.generator.getEntryPosX()+", "+s.generator.getEntryPosZ();
			ReikaJavaLibrary.pConsole(sg);
		}
	}

}
