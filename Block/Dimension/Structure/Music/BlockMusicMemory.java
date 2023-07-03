/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Music;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Decoration.BlockMusicTrigger;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.Music.MusicPuzzle;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMusicMemory extends BlockDimensionStructureTile {

	private final IIcon[] icons = new IIcon[2];

	public BlockMusicMemory(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileMusicMemory();
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/musicmemory");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/musicmemory_front");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		int idx = s == ForgeDirection.NORTH.ordinal() ? 1 : 0;
		return icons[idx];
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
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

	public static class TileMusicMemory extends StructureBlockTile<MusicPuzzleGenerator> {

		private int tick;
		private int index;
		private boolean isPlaying;
		private List<MusicKey> keys = new ArrayList();

		private ForgeDirection facing = ForgeDirection.NORTH;

		private static final int BASE_DELAY = 10; //120 bpm, 8th notes
		private int playSpeed = BASE_DELAY;

		private int correctIndex = -1;

		private int structureIndex;

		public void program(MusicPuzzle m, int idx) {
			keys = m.getMelody();
			playSpeed = m.getPlaySpeed()*BASE_DELAY/8;
			structureIndex = idx;
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
			while (correctIndex < keys.size()-1 && keys.get(correctIndex+1) == null)
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
			if (!worldObj.isRemote)
				this.getGenerator().completePuzzle(structureIndex);
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
						tick = playSpeed;
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
			if (key != null)
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.MUSICPLAY.ordinal(), this, 24, key.ordinal());
			//ReikaJavaLibrary.pConsole(key);
		}

		@SideOnly(Side.CLIENT)
		public void playKeyClient(MusicKey key) {
			double rel = key.getRatio(MusicKey.C5);
			ReikaSoundHelper.playClientSound(ChromaSounds.DING, xCoord+0.5, yCoord+0.5, zCoord+0.5-1, 1, (float)rel);
			ReikaSoundHelper.playClientSound(ChromaSounds.DING, xCoord+0.5, yCoord+0.5, zCoord+0.5+9, 1, (float)rel);
			for (CrystalElement e : CrystalMusicManager.instance.getColorsWithKey(key)) {
				this.playCrystal(e, key);
			}
		}

		@SideOnly(Side.CLIENT)
		private void playCrystal(CrystalElement e, MusicKey key) {
			int dy = yCoord+1;
			int dx = e.ordinal() >= 8 ? xCoord-4 : xCoord+4;
			int dz = zCoord+1+e.ordinal()%8;
			BlockMusicTrigger.createParticle(worldObj, dx, dy, dz, e);
			int idx = CrystalMusicManager.instance.getIntervalFor(e, key);
			double px = dx+0.5;
			if (e.ordinal() >= 8)
				px += 0.75;
			else
				px -= 0.75;
			double pz = dz+0.5;
			double py = yCoord+0.5;
			EntityCCBlurFX fx = new EntityCCBlurFX(worldObj, px, py, pz);
			fx.setColor(e.getColor()).setLife(30).setScale(3F).setRapidExpand().setAlphaFading();
			switch(idx) {
				case 0:
					fx.setIcon(ChromaIcons.RING0);
					break;
				case 1:
					fx.setIcon(ChromaIcons.RING1);
					break;
				case 2:
					fx.setIcon(ChromaIcons.RING2);
					break;
				case 3:
					fx.setIcon(ChromaIcons.RING3);
					break;
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		public void play() {
			if (keys == null)
				return;
			isPlaying = true;
			index = 0;
			tick = playSpeed;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBTTagList li = new NBTTagList();
			for (MusicKey key : keys) {
				li.appendTag(new NBTTagInt(key == null ? -1 : key.ordinal()));
			}

			NBT.setTag("keys", li);

			NBT.setInteger("idx", structureIndex);
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

			structureIndex = NBT.getInteger("idx");
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.MUSIC;
		}

	}

}
