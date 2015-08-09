/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class BlockMusicMemory extends BlockContainer {

	public BlockMusicMemory(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileMusicMemory();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileMusicMemory) {
				((TileMusicMemory)te).play();
			}
		}
		return true;
	}

	public static class TileMusicMemory extends TileEntity {

		private int tick;
		private int index;
		private boolean isPlaying;
		private List<MusicKey> keys;

		private static final int DELAY = 5; //120 bpm, 8th notes

		public void program(MusicPuzzle m) {
			keys = m.getMelody();
		}

		@Override
		public void updateEntity() {
			if (isPlaying) {
				tick--;
				if (tick == 0) {
					if (index == keys.size()-1) {
						isPlaying = false;
						index = 0;
					}
					else {
						this.playKey(keys.get(index));
						index++;
						tick = DELAY;
					}
				}
			}
		}

		private void randomize() {
			keys = new ArrayList();
			int n = ReikaRandomHelper.getRandomPlusMinus(7, 3);
			for (int i = 0; i < n; i++) {
				MusicKey key = this.randomKey();
				keys.add(key);
			}
		}

		private MusicKey randomKey() {
			CrystalElement e = CrystalElement.randomElement();
			List<MusicKey> keys = CrystalMusicManager.instance.getKeys(e);
			return ReikaJavaLibrary.getRandomListEntry(keys);
		}

		private void playKey(MusicKey key) {
			double rel = key.getRatio(MusicKey.C5);
			ChromaSounds.DING.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, 1, (float)rel);
		}

		public void play() {
			if (keys == null)
				return;
			isPlaying = true;
			tick = DELAY;
		}

	}

}
