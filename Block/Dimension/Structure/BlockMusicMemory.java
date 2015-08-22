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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
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
				TileMusicMemory mus = (TileMusicMemory)te;
				if (s == mus.facing.ordinal()) {
					mus.play();
				}
			}
		}
		return true;
	}

	public static void ping(World world, int x, int y, int z, CrystalElement e, int idx) {
		while (world.getBlock(x, y, z-1) == ChromaBlocks.MUSICTRIGGER.getBlockInstance()) {
			z--;
		}
		int meta = world.getBlockMetadata(x, y+1, z);
		if (meta < 8) {
			x -= 4;
		}
		else {
			x += 4;
		}
		z--;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileMusicMemory) {
			MusicKey key = CrystalMusicManager.instance.getKeys(e).get(idx);
			((TileMusicMemory)te).ping(key);
		}
	}

	public static class TileMusicMemory extends TileEntity {

		private int tick;
		private int index;
		private boolean isPlaying;
		private List<MusicKey> keys = new ArrayList();

		private ForgeDirection facing = ForgeDirection.NORTH;

		private static final int DELAY = 10; //120 bpm, 8th notes

		private int correctIndex = -1;

		public void program(MusicPuzzle m) {
			keys = m.getMelody();
		}

		public void ping(MusicKey key) {
			if (keys.isEmpty()) {
				return;
			}

			MusicKey next = keys.get(correctIndex+1);
			if (key == next) {
				this.addCorrect();
			}
			else {
				this.resetCorrect();
			}
		}

		private void addCorrect() {
			correctIndex++;
			if (correctIndex == keys.size()-1) {
				this.complete();
			}
		}

		private void complete() {
			ChromaSounds.CAST.playSoundAtBlock(this, 1, 1);
			correctIndex = -1;

			int dz = zCoord+13;
			/*
			for (int i = -1; i <= 1; i++) {
				int dx = xCoord+i;
				for (int j = 0; j < 3; j++) {
					int dy = yCoord+i;
					worldObj.setBlockMetadataWithNotify(dx, dy, dz, BlockType.CRACKS.metadata, 3);
				}
			}
			 */
			((TileEntityChromaDoor)worldObj.getTileEntity(xCoord, yCoord, dz)).open(-1);
		}

		private void resetCorrect() {
			if (correctIndex != -1)
				ChromaSounds.ERROR.playSoundAtBlock(this, 1, 1);
			correctIndex = -1;
		}

		@Override
		public void updateEntity() {
			if (isPlaying) {
				tick--;
				if (tick == 0) {
					if (index == keys.size()) {
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
		/*
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
		 */
		private void playKey(MusicKey key) {
			double rel = key.getRatio(MusicKey.C5);
			ChromaSounds.DING.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord-1, 1, (float)rel);
			ChromaSounds.DING.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord+9, 1, (float)rel);
			for (CrystalElement e : CrystalMusicManager.instance.getColorsWithKey(key)) {
				this.playCrystal(e);
			}
			//ReikaJavaLibrary.pConsole(key);
		}

		private void playCrystal(CrystalElement e) {
			int dy = yCoord+1;
			int dx = e.ordinal() >= 8 ? xCoord-4 : xCoord+4;
			int dz = zCoord+1+e.ordinal()%8;
			BlockMusicTrigger.createParticle(worldObj, dx, dy, dz, e);
		}

		public void play() {
			if (keys == null)
				return;
			isPlaying = true;
			index = 0;
			tick = DELAY;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBTTagList li = new NBTTagList();
			for (MusicKey key : keys) {
				li.appendTag(new NBTTagInt(key.ordinal()));
			}

			NBT.setTag("keys", li);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			keys.clear();
			NBTTagList li = NBT.getTagList("keys", NBTTypes.INT.ID);
			for (Object o : li.tagList) {
				int idx = ((NBTTagInt)o).func_150287_d();
				keys.add(MusicKey.getByIndex(idx));
			}
		}

	}

}
