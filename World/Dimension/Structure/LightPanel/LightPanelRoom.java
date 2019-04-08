/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.LightPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightPanel;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightSwitch.LightSwitchTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanelGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;


public class LightPanelRoom extends StructurePiece<LightPanelGenerator> {

	public static final int DEPTH = 18;

	public final int rowCount;
	public final int switchCount;

	public final int level;

	private LightState[] switches;
	private final Light[][] lights;

	private final MusicKey[] pitches;
	private final KeySignature key;
	private final List<MusicKey> usablePitches = new ArrayList();

	private Coordinate doorLocation;
	private boolean lastState;

	private final Random rand;

	public LightPanelRoom(LightPanelGenerator s, int r, int sw, int lvl, Random rand, int posX, int posY, int posZ) {
		super(s);
		rowCount = r;
		switchCount = sw;
		level = lvl;

		this.rand = rand;

		lights = new Light[rowCount][LightType.list.length];
		for (int i = 0; i < rowCount; i++) {
			for (int k = 0; k < LightType.list.length; k++) {
				//Coordinate c = new Coordinate(posX+8, posY+1+i, posZ-2+k*2);
				lights[i][k] = new Light(/*c*/);
			}
		}

		pitches = new MusicKey[switchCount];
		key = KeySignature.keys[rand.nextInt(KeySignature.keys.length)];
		for (Note n : key.getScale())
			usablePitches.add(MusicKey.C5.getInterval(n.ordinal()));
		usablePitches.add(MusicKey.C6);
	}

	public final void generatePuzzle() {
		do {
			this.generateConnections();
		} while(!this.isSolvable());
		//ReikaJavaLibrary.pConsole(switches);
	}

	private void generateConnections() {
		switches = new LightState[switchCount];
		for (int i = 0; i < rowCount; i++) {
			for (int k = 0; k < LightType.list.length; k++) {
				lights[i][k].linkedSwitches.clear();
			}
		}

		for (int i = 0; i < switches.length; i++) {
			LightGroup lg = new LightGroup(rowCount);
			switches[i] = new LightState(lg);
		}

		this.doGenerateConnections();
	}

	protected void doGenerateConnections() {
		int[] greenDistrib = new int[switches.length];
		int[] redDistrib = new int[switches.length];
		int[] blueDistrib = new int[switches.length];
		int maxCollect = Math.max(1, 2*rowCount/switchCount);

		for (int i = 0; i < rowCount; i++) {
			int n = rand.nextInt(4) == 0 ? 2 : 1;
			for (int m = 0; m < n; m++) {
				int sw = rand.nextInt(switchCount);
				while (greenDistrib[sw] > maxCollect)
					sw = rand.nextInt(switchCount);
				this.addConnection(sw, i, LightType.TARGET);
				greenDistrib[sw]++;
			}
		}
		ArrayList<Integer> reds = new ArrayList();
		int nred = Math.max(2, Math.min(rowCount-1, rowCount/2+ReikaRandomHelper.getRandomPlusMinus(0, 2)));
		int nredleft = Math.max(1, rand.nextInt(nred/2));
		int nblue = nred-nredleft;
		for (int i = 0; i < nred; i++) {
			int idx = rand.nextInt(switchCount);
			while (reds.contains(idx)) {
				idx = rand.nextInt(switchCount);
			}
			reds.add(idx);
			int sw = rand.nextInt(switchCount);
			while (redDistrib[sw] > maxCollect)
				sw = rand.nextInt(switchCount);
			this.addConnection(sw, idx, LightType.BLOCK);
			redDistrib[sw]++;
		}
		for (int i = 0; i < nblue; i++) {
			int idx = reds.remove(rand.nextInt(reds.size()));
			int sw = rand.nextInt(switchCount);
			while (blueDistrib[sw] > maxCollect)
				sw = rand.nextInt(switchCount);
			this.addConnection(sw, idx, LightType.CANCEL);
			blueDistrib[sw]++;
		}
		/*
		for (int i = 0; i < switches.length; i++) {
			LightGroup lg = new LightGroup(rowCount);
			int ng = 1+rand.nextInt(3);
			int nr = rand.nextInt(3);
			int nb = rand.nextInt(4);
			for (int k = 0; k < ng; k++) {
				lg.addLight(rand.nextInt(rowCount), LightType.TARGET);
			}
			for (int k = 0; k < nr; k++) {
				lg.addLight(rand.nextInt(rowCount), LightType.BLOCK);
			}
			for (int k = 0; k < nb; k++) {
				lg.addLight(rand.nextInt(rowCount), LightType.CANCEL);
			}
			switches[i] = new LightState(lg);
		}
		 */
	}

	protected final void addConnection(int sw, int row, LightType type) {
		switches[sw].group.addLight(row, type);
		lights[row][type.ordinal()].linkedSwitches.add(sw);
		if (pitches[sw] == null) {
			this.addPitch(sw);
		}
	}

	private void addPitch(int sw) {
		int idx = rand.nextInt(usablePitches.size());
		MusicKey key = usablePitches.get(idx);
		//while (isKeyMajor != isSwitchRequired(sw)) {
		//	idx = rand.nextInt(usablePitches.size());
		//}
		usablePitches.remove(idx);
		pitches[sw] = key;
	}

	protected boolean isSolvable() {
		//int combos = ReikaMathLibrary.intpow2(2, switchCount)-1;
		ArrayList<boolean[]> li = this.getCombosThatLightAllGreen();
		for (boolean[] state : li) {
			//boolean[] state = ReikaArrayHelper.booleanFromBitflags(i, switchCount);
			for (int k = 0; k < switches.length; k++) {
				switches[k].active = state[k];
			}
			if (this.isComplete()) {
				this.reset();
				return true;
			}
		}
		this.reset();
		return false;
	}

	protected final ArrayList<boolean[]> getCombosThatLightAllGreen() {
		ArrayList<boolean[]> li = new ArrayList();
		li.add(new boolean[switchCount]);

		for (int i = 0; i < rowCount; i++) {
			Light l = lights[i][LightType.TARGET.ordinal()];
			ArrayList<boolean[]> repl = new ArrayList();
			for (int sw : l.linkedSwitches) {
				for (boolean[] orig : li) {
					boolean[] state = Arrays.copyOf(orig, orig.length);
					state[sw] = true;
					repl.add(state);
				}
			}
			li = repl;
		}

		return li;
	}

	private void reset() {
		for (int k = 0; k < switches.length; k++) {
			switches[k].active = false;
		}
	}

	public final void toggleSwitch(World world, int x, int y, int z, int sw, boolean active) {
		switches[sw].active = active;
		this.updateLights(world);
		if (active)
			ChromaSounds.DING.playSoundAtBlock(world, x, y, z, 2, (float)CrystalMusicManager.instance.getPitchFactor(pitches[sw]));
	}

	public final int getWidth() {
		return (switchCount+switchCount-1)/2+3; //switches + gaps + wall space
	}

	public final int getHeight() {
		return rowCount+4+2+1;
	}

	private void updateLights(World world) {
		for (int i = 0; i < rowCount; i++) {
			for (int k = 0; k < LightType.list.length; k++) {
				boolean flag = false;
				for (int n = 0; n < switchCount; n++) {
					if (switches[n].active && switches[n].group.containsLight(i, LightType.list[k])) {
						flag = true;
						break;
					}
				}
				lights[i][k].state = flag;
				BlockLightPanel.activate(world, lights[i][k].location.xCoord, lights[i][k].location.yCoord, lights[i][k].location.zCoord, flag);
			}
		}
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(lights));
		boolean flag = this.isComplete();
		if (flag != lastState)
			this.updateDoor(world, flag);
		lastState = flag;
	}

	public void updateDoor(World world, boolean open) {
		if (doorLocation != null) {
			TileEntity te = doorLocation.getTileEntity(world);
			TileEntityChromaDoor td = (TileEntityChromaDoor)te;
			if (open)
				td.open(0);
			else
				td.close();
		}
	}

	@Override
	public final void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int r = this.getWidth();
		for (int k = -r; k <= r; k++) {
			int dz = z+k;
			for (int i = 0; i <= DEPTH; i++) {
				int dx = x+i;
				int h = this.getHeight();
				for (int j = 0; j <= h; j++) {
					if (i == 0 || i == DEPTH || k == -r || k == r || j == 0 || j == h) {
						world.setBlock(dx, y+j, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					}
					else {
						world.setBlock(dx, y+j, dz, Blocks.air);
					}
				}
			}
		}

		int dx = x+DEPTH*2/3;

		for (int i = -this.getWidth(); i <= this.getWidth(); i++) {
			for (int k = 1; k < this.getHeight(); k++) {
				world.setBlock(dx, y+k, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
		}

		for (int i = 0; i < rowCount; i++) {
			int dy = y+1+4+1+i;
			for (int l = 0; l < LightType.list.length; l++) {
				int dz = z-LightType.list.length+l*2+1;
				this.placeLight(world, dx, dy, dz, i, LightType.list[l]);
				world.setBlock(dx, dy, dz+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			}
		}

		for (int i = -1; i <= rowCount; i++) {
			int dy = y+1+1+i+4;
			world.setBlock(dx, dy, z-3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			world.setBlock(dx, dy, z-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			world.setBlock(dx, dy, z+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
			world.setBlock(dx, dy, z+3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);

			world.setBlock(dx, dy, z-LightType.list.length-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(dx, dy, z+LightType.list.length+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		}

		for (int i = -this.getWidth(); i <= this.getWidth(); i++) {
			for (int k = 1; k <= 4; k++) {
				world.setBlock(dx, y+k, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}

			if (Math.abs(i) < Math.abs(LightType.list.length*2-1)) {
				int mb = Math.abs(i) == Math.abs(LightType.list.length*2-1)-1 ? BlockType.STONE.metadata : BlockType.CLOAK.metadata;
				world.setBlock(dx, y+1+4, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), mb);
				world.setBlock(dx, y+1+4+rowCount+1, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), mb);
			}
		}

		for (int i = -2; i <= 2; i++) {
			for (int k = 1; k <= 3; k++) {
				world.setBlock(dx, y+k, z+i, ChromaBlocks.DOOR.getBlockInstance(), BlockChromaDoor.getMetadata(false, false, false, true));
			}
		}

		doorLocation = new Coordinate(dx, y+1, z);

		dx = x+DEPTH*1/3;

		for (int k = 0; k < switchCount; k++) {
			int dy = y+1;
			int dz = z-switchCount*2/2+k*2+1;
			world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(dx, dy+1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			if (k != switchCount-1) {
				world.setBlock(dx, dy, dz+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(dx, dy+1, dz+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
			}
		}

		for (int k = 0; k < switchCount; k++) {
			int dy = y+1+1;
			int dz = z-switchCount*2/2+k*2+1;
			this.placeSwitch(world, dx, dy, dz, k);
		}

		if (level == 0)
			parent.generatePasswordTile(dx-5, y+1, z);

		for (int i = -2; i <= 2; i++) {
			for (int k = 1; k <= 3; k++) {
				world.setBlock(x, y+k, z+i, Blocks.air);
				world.setBlock(x+DEPTH, y+k, z+i, Blocks.air);
			}
		}
		world.setBlock(x, y+4, z+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x, y+4, z-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x, y+2, z-3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x, y+2, z+3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		for (int k = -this.getWidth(); k <= this.getWidth(); k++) {
			for (int i = DEPTH*2/3+1; i <= DEPTH; i++) {
				int j = 5;
				if (Math.abs(k) >= this.getWidth()-1) {
					j = 3;
				}
				else if (Math.abs(k) >= this.getWidth()-3) {
					j = 4;
				}
				world.setBlock(x+i, y+1+j, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
		}

		Object[] items = {ChromaStacks.lumaDust, 70, ChromaStacks.chargedBlueShard, 90, ChromaStacks.lumenGem, 50, ChromaStacks.glowChunk, 10};
		parent.generateLootChest(x+DEPTH-3, y+1, z-this.getWidth()+1, ForgeDirection.SOUTH, ChestGenHooks.STRONGHOLD_CORRIDOR, 0, items);
		parent.generateLootChest(x+DEPTH-3, y+1, z+this.getWidth()-1, ForgeDirection.NORTH, ChestGenHooks.STRONGHOLD_CORRIDOR, 0, items);

		world.setBlock(x+DEPTH-3, y, z-this.getWidth()+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(x+DEPTH-3, y, z+this.getWidth()-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
	}

	private void placeSwitch(ChunkSplicedGenerationCache world, int x, int y, int z, int sw) {
		world.setTileEntity(x, y, z, ChromaBlocks.PANELSWITCH.getBlockInstance(), 0, new SwitchCallback(parent.id, level, sw));
	}

	private void placeLight(ChunkSplicedGenerationCache world, int x, int y, int z, int row, LightType type) {
		Coordinate c = new Coordinate(x, y, z);
		lights[row][type.ordinal()].location = c;
		world.setBlock(x, y, z, ChromaBlocks.LIGHTPANEL.getBlockInstance(), type.ordinal()*2);
	}

	public final boolean isComplete() {
		for (int i = 0; i < rowCount; i++) {
			if (!lights[i][LightType.TARGET.ordinal()].state)
				return false;
			if (lights[i][LightType.BLOCK.ordinal()].state && !lights[i][LightType.CANCEL.ordinal()].state)
				return false;
		}
		return true;
	}

	protected static class LightState {

		private final LightGroup group;
		private boolean active;

		private LightState(LightGroup lg) {
			group = lg;
		}

		@Override
		public String toString() {
			return group.toString();
		}

	}

	protected static class Light {

		private /*final*/ Coordinate location;
		private final HashSet<Integer> linkedSwitches = new HashSet();
		private boolean state;

		private Light(/*Coordinate c*/) {
			//location = c;
		}

		@Override
		public String toString() {
			return String.valueOf(state);
		}

	}

	private static class SwitchCallback implements TileCallback {

		private final UUID uid;
		private final int level;
		private final int channel;

		private SwitchCallback(UUID id, int lvl, int ch) {
			uid = id;
			level = lvl;
			channel = ch;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof LightSwitchTile) {
				((LightSwitchTile)te).setData(level, channel);
				((LightSwitchTile)te).uid = uid;
			}
		}

	}

}
