/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockMusicMemory.TileMusicMemory;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator.MelodyPrefab;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class MusicPuzzle extends StructurePiece implements TileCallback {

	private final LinkedList<MusicKey> melody = new LinkedList();

	private int length;
	private CrystalElement center;
	private KeySignature signature;
	//private ArrayList<CrystalElement> validColors;
	private ArrayList<MusicKey> validNotes;

	private MusicKey lastKey;

	private final MusicPuzzleBlocks blocks;

	private final int index;

	public boolean isSolved = false;

	public MusicPuzzle(DimensionStructureGenerator s, int len, int idx) {
		super(s);
		length = len;
		index = idx;

		blocks = new MusicPuzzleBlocks();
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		blocks.generate(world, x, y, z);

		Block tr = ChromaBlocks.MUSICTRIGGER.getBlockInstance();
		Block dr = ChromaBlocks.DOOR.getBlockInstance();

		world.setBlock(x+6, y+1, z+18, dr);
		world.setBlock(x+6, y+2, z+18, dr);
		world.setBlock(x+6, y+3, z+18, dr);
		world.setBlock(x+4, y+1, z+18, dr);
		world.setBlock(x+4, y+2, z+18, dr);
		world.setBlock(x+4, y+3, z+18, dr);
		world.setBlock(x+5, y+1, z+18, dr);
		world.setBlock(x+5, y+2, z+18, dr);
		world.setBlock(x+5, y+3, z+18, dr);

		world.setBlock(x+1, y+1, z+6, tr);
		world.setBlock(x+1, y+1, z+7, tr);
		world.setBlock(x+1, y+1, z+8, tr);
		world.setBlock(x+1, y+1, z+9, tr);
		world.setBlock(x+1, y+1, z+10, tr);
		world.setBlock(x+1, y+1, z+11, tr);
		world.setBlock(x+1, y+1, z+12, tr);
		world.setBlock(x+1, y+1, z+13, tr);
		world.setBlock(x+9, y+1, z+6, tr);
		world.setBlock(x+9, y+1, z+7, tr);
		world.setBlock(x+9, y+1, z+8, tr);
		world.setBlock(x+9, y+1, z+9, tr);
		world.setBlock(x+9, y+1, z+10, tr);
		world.setBlock(x+9, y+1, z+11, tr);
		world.setBlock(x+9, y+1, z+12, tr);
		world.setBlock(x+9, y+1, z+13, tr);

		world.setTileEntity(x+5, y+1, z+5, ChromaBlocks.MUSICMEMORY.getBlockInstance(), 0, this);
	}

	public void initialize(Random rand) {
		center = CrystalElement.randomElement();
		signature = CrystalMusicManager.instance.getSignature(center);
		validNotes = new ArrayList(CrystalMusicManager.instance.getValidNotesToMixWith(center));
		//validColors = new ArrayList(CrystalMusicManager.instance.getChordMixes(signature));
		//validColors.add(center); //double the probability of this one

		for (int i = 0; i < length; i++) {
			MusicKey key = this.randomKey(rand);
			while (!this.keyIsCurrentlyValid(key))
				key = this.randomKey(rand);
			melody.add(key);

			lastKey = key;
		}
	}

	private boolean keyIsCurrentlyValid(MusicKey key) {
		if (lastKey == null)
			return true;
		int diff = key.ordinal()-lastKey.ordinal();
		if (diff > 12 || diff < -12) //nothing over an octave
			return false;
		int mod = diff%12;
		if (mod == 11) //7th
			return false;
		if (mod == 6) //tritone
			return false;
		return true;
	}

	public void loadPrefab(MelodyPrefab pre) {
		List<MusicKey> li = pre.getNotes();
		melody.clear();
		melody.addAll(li);
		length = li.size();
		signature = pre.key;
		center = pre.center;
	}

	public List<MusicKey> getMelody() {
		return Collections.unmodifiableList(melody);
	}

	public boolean compare(ArrayList<MusicKey> li) {
		return li.equals(melody);
	}

	private MusicKey randomKey(Random rand) {
		//CrystalElement e = validColors.get(rand.nextInt(validColors.size()));//CrystalElement.randomElement();
		//List<MusicKey> keys = CrystalMusicManager.instance.getKeys(e);
		//return keys.get(rand.nextInt(keys.size()));
		return validNotes.get(rand.nextInt(validNotes.size()));
	}

	@Override
	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof TileMusicMemory) {
			((TileMusicMemory)te).program(this, index);
			((TileMusicMemory)te).uid = parent.id;
		}
	}

}
