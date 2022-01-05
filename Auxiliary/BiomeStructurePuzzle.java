package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightSwitch;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightSwitch.LightSwitchTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockLockKey.TileEntityLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.ShiftMaze.BlockShiftLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.ShiftMaze.BlockShiftLock.Passability;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.FragmentStructureData;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator.MelodyPrefab;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeStructurePuzzle implements FragmentStructureData {

	private static final ArrayList<Coordinate> runeLocations = new ArrayList();

	static {
		runeLocations.add(new Coordinate(5, 5, 6));
		runeLocations.add(new Coordinate(6, 5, 5));
		runeLocations.add(new Coordinate(-5, 5, 6));
		runeLocations.add(new Coordinate(-6, 5, 5));
		runeLocations.add(new Coordinate(5, 5, -6));
		runeLocations.add(new Coordinate(6, 5, -5));
		runeLocations.add(new Coordinate(-5, 5, -6));
		runeLocations.add(new Coordinate(-6, 5, -5));
	}

	private final ArrayList<MusicKey> melody = new ArrayList();
	private final SwitchGroup[] doorKeys = new SwitchGroup[4];
	private final ArrayList<CrystalElement> crystalColors = new ArrayList();
	private final ArrayList<CrystalElement> doorColors = new ArrayList();

	private int keyIndex;

	private final HashSet<SwitchGroup> usedKeys = new HashSet();

	private final HashMap<Coordinate, CrystalElement> runes = new HashMap();

	//private boolean isPlayingMelody;
	private long musicTick;
	private long nextNoteTick = 10;
	private int melodyIndex;

	private boolean complete;

	private final ArrayList<MusicKey> remainingGuess = new ArrayList();

	public void clear() {
		melody.clear();
		crystalColors.clear();
		doorColors.clear();
		runes.clear();
		usedKeys.clear();

		remainingGuess.clear();
	}

	public void generate(Random rand) {
		keyIndex = rand.nextInt(8);

		for (int i = 0; i < doorKeys.length; i++) {
			ForgeDirection ns = i <= 1 ? ForgeDirection.NORTH : ForgeDirection.SOUTH;
			ForgeDirection ew = i%2 == 0 ? ForgeDirection.WEST : ForgeDirection.EAST;
			SwitchGroup key = SwitchGroup.random(ns, ew, rand);
			while (usedKeys.contains(key) || key.isSameCorner())
				key = SwitchGroup.random(ns, ew, rand);
			doorKeys[i] = key;
			usedKeys.add(key);
		}

		ArrayList<CrystalElement> li = ReikaJavaLibrary.makeListFromArray(CrystalElement.elements);
		for (int i = 0; i < 8; i++) {
			doorColors.add(li.remove(rand.nextInt(li.size())));
		}

		ArrayList<Integer> idx = ReikaJavaLibrary.makeIntListFromArray(ReikaArrayHelper.getLinearArray(8));
		Collections.shuffle(idx);

		for (Coordinate c : runeLocations) {
			runes.put(c, doorColors.get(idx.remove(0)));
		}

		HashSet<MelodyPrefab> excl = new HashSet();
		while (melody.isEmpty()) {
			int attempts = 0;
			MelodyPrefab pre = MusicPuzzleGenerator.getRandomPrefab(rand, Integer.MAX_VALUE, excl);
			melody.addAll(pre.getNotes());
			while (attempts < 10 && !this.calculateCrystals(rand)) {
				crystalColors.clear();
				attempts++;
			}
			if (attempts >= 10) {
				crystalColors.clear();
				melody.clear();
				excl.add(pre);
				ChromatiCraft.logger.log("Failed to perform "+pre+" with only eight colors");
			}
		}


		remainingGuess.addAll(melody);
	}

	private boolean calculateCrystals(Random rand) {
		HashSet<MusicKey> needed = new HashSet(melody);
		while (!needed.isEmpty()) {
			CrystalElement e = this.findMostEffective(needed);
			if (e == null)
				return false;
			crystalColors.add(e);
			needed.removeAll(CrystalMusicManager.instance.getKeys(e));
		}
		if (crystalColors.size() > 8) {
			;//throw new RuntimeException(melody+" > "+crystalColors);
			return false;
		}
		while (crystalColors.size() < 8) {
			CrystalElement e = CrystalElement.elements[rand.nextInt(16)];
			while (crystalColors.contains(e))
				e = CrystalElement.elements[rand.nextInt(16)];
			crystalColors.add(e);
		}
		return true;
	}

	public void addToArray(FilledBlockArray array, int x0, int y0, int z0) {
		Coordinate root = new Coordinate(x0, y0, z0);
		for (Entry<Coordinate, CrystalElement> e : runes.entrySet()) {
			Coordinate c = e.getKey().offset(x0, y0, z0);
			array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.RUNE.getBlockInstance(), e.getValue().ordinal());
		}

		for (int i = 0; i < 4; i++) {
			for (Coordinate c : this.getColorDoorLocations(root, i)) {
				array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.COLORLOCK.getBlockInstance());
			}
		}

		for (int i = 0; i < 4; i++) {
			for (Entry<Coordinate, ForgeDirection> e : this.getSwitchDoorLocations(root, i).entrySet()) {
				Coordinate c = e.getKey();
				array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.SHIFTLOCK.getBlockInstance(), Passability.CLOSED.ordinal());
			}
		}

		for (Coordinate c : this.getSwitchLocations(root)) {
			array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.PANELSWITCH.getBlockInstance());
			Coordinate red = c.offset(0, 1, 0);
			Coordinate green = c.offset(0, -1, 0);
			array.setBlock(red.xCoord, red.yCoord, red.zCoord, ChromaBlocks.LIGHTPANEL.getBlockInstance(), 3);
			array.setBlock(green.xCoord, green.yCoord, green.zCoord, ChromaBlocks.LIGHTPANEL.getBlockInstance(), 0);
		}

		for (int i = 2; i < 4/*6*/; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x0+dir.offsetX*2;
			int dy = y0+5;
			int dz = z0+dir.offsetZ*2;
			array.setBlock(dx, dy, dz, ChromaBlocks.LOCKKEY.getBlockInstance(), keyIndex);
		}

		for (Coordinate c : this.getBarrierLocations(root)) {
			array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.DOOR.getBlockInstance());
		}

		for (int i = 0; i < 4; i++) {
			for (Entry<Coordinate, CrystalElement> e : this.getCrystalLocations(root).entrySet()) {
				Coordinate c = e.getKey();
				array.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.LAMP.getBlockInstance(), e.getValue().ordinal());
			}
		}

		for (Coordinate c : this.getLiquidLocations(root)) {
			array.setBlock(c.xCoord, c.yCoord, c.zCoord, Blocks.water);
		}
	}

	public void placeData(World world, TileEntityStructControl root) {
		Coordinate ref = new Coordinate(root);

		for (Entry<Coordinate, CrystalElement> e : runes.entrySet()) {
			Coordinate c = e.getKey().offset(root.xCoord, root.yCoord, root.zCoord);
			c.setBlock(world, ChromaBlocks.RUNE.getBlockInstance(), e.getValue().ordinal());
		}

		for (int i = 0; i < 4; i++) {
			for (Coordinate c : this.getColorDoorLocations(ref, i)) {
				c.setBlock(world, ChromaBlocks.COLORLOCK.getBlockInstance());
				TileEntityColorLock te = (TileEntityColorLock)c.getTileEntity(world);
				if (te == null) {
					te = new TileEntityColorLock();
					world.setTileEntity(c.xCoord, c.yCoord, c.zCoord, te);
				}
				te.setColors(doorColors.get(i*2), doorColors.get(i*2+1));
			}
		}

		for (int i = 0; i < 4; i++) {
			for (Entry<Coordinate, ForgeDirection> e : this.getSwitchDoorLocations(ref, i).entrySet()) {
				SwitchGroup gr = doorKeys[i];
				Passability p = Passability.getDirectionalPassability(e.getValue(), false);
				p = Passability.CLOSED;
				e.getKey().setBlock(world, ChromaBlocks.SHIFTLOCK.getBlockInstance(), p.ordinal());
			}
		}

		for (Coordinate c : this.getSwitchLocations(ref)) {
			c.setBlock(world, ChromaBlocks.PANELSWITCH.getBlockInstance());
			LightSwitchTile te = (LightSwitchTile)c.getTileEntity(world);
			te.setDelegate(ref);
			Coordinate red = c.offset(0, 1, 0);
			Coordinate green = c.offset(0, -1, 0);
			red.setBlock(world, ChromaBlocks.LIGHTPANEL.getBlockInstance(), 3);
			green.setBlock(world, ChromaBlocks.LIGHTPANEL.getBlockInstance(), 0);
		}

		for (int i = 2; i < 4/*6*/; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = root.xCoord+dir.offsetX*2;
			int dy = root.yCoord+5;
			int dz = root.zCoord+dir.offsetZ*2;
			world.setBlock(dx, dy, dz, ChromaBlocks.LOCKKEY.getBlockInstance(), keyIndex, 3);
			TileEntityLockKey te = (TileEntityLockKey)world.getTileEntity(dx, dy, dz);
			te.setDelegate(ref);
		}

		for (Coordinate c : this.getBarrierLocations(ref)) {
			c.setBlock(world, ChromaBlocks.DOOR.getBlockInstance());
		}

		for (int i = 0; i < 4; i++) {
			for (Entry<Coordinate, CrystalElement> e : this.getCrystalLocations(ref).entrySet()) {
				e.getKey().setBlock(world, ChromaBlocks.LAMP.getBlockInstance(), e.getValue().ordinal());
			}
		}

		Block b = this.getLiquid(world, ref);
		for (Coordinate c : this.getLiquidLocations(ref)) {
			c.setBlock(world, b);
		}
	}

	private Block getLiquid(World world, Coordinate root) {
		BiomeGenBase b = ReikaWorldHelper.getNaturalGennedBiomeAt(world, root.xCoord, root.zCoord);
		if (ChromatiCraft.isRainbowForest(b))
			return ChromaBlocks.CHROMA.getBlockInstance();
		if (ChromatiCraft.isEnderForest(b))
			return FluidRegistry.getFluid("ender").getBlock();
		if (BiomeGlowingCliffs.isGlowingCliffs(b))
			return ChromaBlocks.LUMA.getBlockInstance();
		return Blocks.lava;
	}

	private CrystalElement findMostEffective(HashSet<MusicKey> needed) {
		CrystalElement best = null;
		int bestAmt = 0;
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (crystalColors.contains(e))
				continue;
			ArrayList<MusicKey> li = new ArrayList(CrystalMusicManager.instance.getKeys(e));
			li.retainAll(needed);
			if (li.size() > bestAmt) {
				best = e;
				bestAmt = li.size();
			}
		}
		return best;
	}

	private static class SwitchGroup {

		private final ForgeDirection areaNS;
		private final ForgeDirection areaEW;

		private final boolean NW;
		private final boolean SW;
		private final boolean NE;
		private final boolean SE;

		private SwitchGroup(ForgeDirection ns, ForgeDirection ew, boolean nw, boolean sw, boolean ne, boolean se) {
			areaNS = ns;
			areaEW = ew;

			NW = nw;
			SW = sw;
			NE = ne;
			SE = se;
		}

		public boolean isSameCorner() {
			boolean c1 = areaNS == ForgeDirection.NORTH && areaEW == ForgeDirection.WEST && NW && !SW && !NE && !SE;
			boolean c2 = areaNS == ForgeDirection.SOUTH && areaEW == ForgeDirection.WEST && !NW && SW && !NE && !SE;
			boolean c3 = areaNS == ForgeDirection.NORTH && areaEW == ForgeDirection.EAST && !NW && !SW && NE && !SE;
			boolean c4 = areaNS == ForgeDirection.SOUTH && areaEW == ForgeDirection.EAST && !NW && !SW && !NE && SE;
			return c1 || c2 || c3 || c4;
		}

		private static SwitchGroup random(ForgeDirection ns, ForgeDirection ew, Random rand) {
			return new SwitchGroup(ns, ew, rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean());
		}

		@Override
		public int hashCode() {
			return ((NW ? 1 : 0) << 0) | ((SW ? 1 : 0) << 1) | ((NE ? 1 : 0) << 2) | ((SE ? 1 : 0) << 3);
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof SwitchGroup && this.matchKeys((SwitchGroup)o);
		}

		private boolean matchKeys(SwitchGroup o) {
			return o.NW == NW && o.NE == NE && o.SE == SE && o.SW == SW;
		}

		private NBTTagCompound writeToTag() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("nw", NW);
			tag.setBoolean("ne", NE);
			tag.setBoolean("sw", SW);
			tag.setBoolean("se", SE);

			tag.setInteger("ew", areaEW.ordinal());
			tag.setInteger("ns", areaNS.ordinal());
			return tag;
		}

		private static SwitchGroup readTag(NBTTagCompound tag) {
			ForgeDirection ns = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("ns")];
			ForgeDirection ew = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("ew")];
			return new SwitchGroup(ns, ew, tag.getBoolean("nw"), tag.getBoolean("sw"), tag.getBoolean("ne"), tag.getBoolean("se"));
		}

		public boolean validate(World world, Coordinate switchNW, Coordinate switchSW, Coordinate switchNE, Coordinate switchSE) {
			boolean nw = NW == BlockLightSwitch.isSwitchUp(world, switchNW.xCoord, switchNW.yCoord, switchNW.zCoord);
			boolean sw = SW == BlockLightSwitch.isSwitchUp(world, switchSW.xCoord, switchSW.yCoord, switchSW.zCoord);
			boolean ne = NE == BlockLightSwitch.isSwitchUp(world, switchNE.xCoord, switchNE.yCoord, switchNE.zCoord);
			boolean se = SE == BlockLightSwitch.isSwitchUp(world, switchSE.xCoord, switchSE.yCoord, switchSE.zCoord);
			return nw && sw && ne && se;
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		NBTTagList li = new NBTTagList();
		for (MusicKey m : melody) {
			li.appendTag(new NBTTagInt(m.ordinal()));
		}
		NBT.setTag("melody", li);

		li = new NBTTagList();
		for (CrystalElement e : crystalColors) {
			li.appendTag(new NBTTagInt(e.ordinal()));
		}
		NBT.setTag("crystals", li);

		li = new NBTTagList();
		for (CrystalElement e : doorColors) {
			li.appendTag(new NBTTagInt(e.ordinal()));
		}
		NBT.setTag("doorColors", li);

		li = new NBTTagList();
		for (Entry<Coordinate, CrystalElement> e : runes.entrySet()) {
			NBTTagCompound tag = e.getKey().writeToTag();
			tag.setInteger("color", e.getValue().ordinal());
			li.appendTag(tag);
		}
		NBT.setTag("runes", li);

		for (int i = 0; i < doorKeys.length; i++) {
			NBT.setTag("door"+i, doorKeys[i].writeToTag());
		}

		NBT.setInteger("key", keyIndex);
		NBT.setBoolean("complete", complete);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		this.clear();

		NBTTagList li = NBT.getTagList("melody", NBTTypes.INT.ID);
		for (Object o : li.tagList) {
			NBTTagInt i = (NBTTagInt)o;
			melody.add(MusicKey.getByIndex(i.func_150287_d()));
		}

		li = NBT.getTagList("crystals", NBTTypes.INT.ID);
		for (Object o : li.tagList) {
			NBTTagInt i = (NBTTagInt)o;
			crystalColors.add(CrystalElement.elements[i.func_150287_d()]);
		}

		li = NBT.getTagList("doorColors", NBTTypes.INT.ID);
		for (Object o : li.tagList) {
			NBTTagInt i = (NBTTagInt)o;
			doorColors.add(CrystalElement.elements[i.func_150287_d()]);
		}

		li = NBT.getTagList("runes", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Coordinate c = Coordinate.readTag(tag);
			CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
			runes.put(c, e);
		}

		for (int i = 0; i < doorKeys.length; i++) {
			doorKeys[i] = SwitchGroup.readTag(NBT.getCompoundTag("door"+i));
		}

		keyIndex = NBT.getInteger("key");
		complete = NBT.getBoolean("complete");

		remainingGuess.addAll(melody);
	}

	@Override
	public void handleTileAdd(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root) {
		if (te instanceof TileEntityLockKey) {
			this.updateColorDoors(world, new Coordinate(root));
		}
	}

	@Override
	public void handleTileRemove(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root) {
		if (te instanceof TileEntityLockKey) {
			this.updateColorDoors(world, new Coordinate(root));
		}
	}

	@Override
	public void handleTileInteract(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root, EntityPlayer ep) {
		if (te instanceof LightSwitchTile) {
			Coordinate c = new Coordinate(root);
			if (ProgressStage.CTM.isPlayerAtStage(ep)) {
				for (int i = 0; i < 4; i++) {
					this.setDoors(world, c, i, true);
				}
			}
			else if (!ProgressionManager.instance.playerHasPrerequisites(ep, ProgressStage.BIOMESTRUCT)) {
				world.setBlockMetadataWithNotify(x, y, z, 0, 2);
				ChromaSounds.ERROR.playSoundAtBlock(world, x, y, z);
			}
			else {
				this.updateSwitchDoors(world, c);
			}
		}
	}

	@Override
	public boolean isInaccessible(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root, EntityPlayer ep) {
		return !ProgressionManager.instance.playerHasPrerequisites(ep, ProgressStage.BIOMESTRUCT);
	}

	@Override
	public void handleMusicTrigger(World world, int x, int y, int z, CrystalElement e, MusicKey mk, TileEntityStructControl root, EntityPlayer ep) {
		if (complete)
			return;
		if (mk == remainingGuess.get(0) || ProgressStage.CTM.isPlayerAtStage(ep)) {
			remainingGuess.remove(0);
			if (remainingGuess.isEmpty()) {
				this.complete(world, root, ep);
			}
		}
		else {
			remainingGuess.clear();
			remainingGuess.addAll(melody);
			ChromaSounds.ERROR.playSoundAtBlock(root);
		}
	}

	@Override
	public void onTileLoaded(TileEntityStructControl root) {
		if (!root.worldObj.isRemote)
			this.updateColorDoors(root.worldObj, new Coordinate(root));
	}

	private void complete(World world, TileEntityStructControl root, EntityPlayer ep) {
		complete = true;
		ChromaSounds.CAST.playSoundAtBlock(root);
		Coordinate ref = new Coordinate(root);
		for (Coordinate c : this.getBarrierLocations(ref)) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, true);
		}
		for (Coordinate c : this.getLowerChestLocations(ref)) {
			if (c.getBlock(world) == ChromaBlocks.LOOTCHEST.getBlockInstance()) {
				c.setBlockMetadata(world, c.getBlockMetadata(world)%8);
			}
		}
	}

	private void updateColorDoors(World world, Coordinate root) {
		HashSet<CrystalElement> opened = new HashSet();
		if (runes.isEmpty()) {
			this.reconstructRunes(world, root);
		}
		for (Entry<Coordinate, CrystalElement> e : runes.entrySet()) {
			Coordinate c = e.getKey().offset(root.xCoord, root.yCoord+1, root.zCoord);
			if (c.getBlock(world) == ChromaBlocks.LOCKKEY.getBlockInstance()) {
				opened.add(e.getValue());
			}
		}
		for (int i = 0; i < 4; i++) {
			for (Coordinate c : this.getColorDoorLocations(root, i)) {
				TileEntityColorLock te = (TileEntityColorLock)c.getTileEntity(world);
				if (te == null)
					continue;
				te.setOpenColors(opened);
			}
		}
	}

	private void reconstructRunes(World world, Coordinate root) {
		for (Coordinate c : runeLocations) {
			runes.put(c, CrystalElement.elements[c.offset(root).getBlockMetadata(world)]);
		}
	}

	private void updateSwitchDoors(World world, Coordinate root) {
		for (int i = 0; i < 4; i++) {
			boolean flag = doorKeys[i].validate(world, this.switchLoc(root, ForgeDirection.NORTH, ForgeDirection.WEST), this.switchLoc(root, ForgeDirection.SOUTH, ForgeDirection.WEST), this.switchLoc(root, ForgeDirection.NORTH, ForgeDirection.EAST), this.switchLoc(root, ForgeDirection.SOUTH, ForgeDirection.EAST));
			this.setDoors(world, root, i, flag);
		}
	}

	private void setDoors(World world, Coordinate root, int i, boolean open) {
		Coordinate sw = this.switchLoc(root, doorKeys[i].areaNS, doorKeys[i].areaEW);
		sw.offset(0, 1, 0).setBlockMetadata(world, open ? 2 : 3);
		sw.offset(0, -1, 0).setBlockMetadata(world, open ? 1 : 0);
		for (Coordinate c : this.getSwitchDoorLocations(root, i).keySet()) {
			BlockShiftLock.setOpen(world, c.xCoord, c.yCoord, c.zCoord, open);
		}
	}

	private Collection<Coordinate> getSwitchLocations(Coordinate root) {
		ArrayList<Coordinate> ret = new ArrayList();
		ret.add(this.switchLoc(root, ForgeDirection.NORTH, ForgeDirection.EAST));
		ret.add(this.switchLoc(root, ForgeDirection.NORTH, ForgeDirection.WEST));
		ret.add(this.switchLoc(root, ForgeDirection.SOUTH, ForgeDirection.EAST));
		ret.add(this.switchLoc(root, ForgeDirection.SOUTH, ForgeDirection.WEST));
		return ret;
	}

	private Coordinate switchLoc(Coordinate root, ForgeDirection ns, ForgeDirection ew) {
		return root.offset(ns.offsetZ*3, 8, ew.offsetX*3);
	}

	private Collection<Coordinate> getColorDoorLocations(Coordinate root, int i) {
		ArrayList<Coordinate> ret = new ArrayList();
		for (int y = 2; y <= 4; y++) {
			for (int d = 1; d <= 2; d++) {
				switch(i) {
					case 0:
						ret.add(root.offset(d, y, -3));
						break;
					case 1:
						ret.add(root.offset(3, y, d));
						break;
					case 2:
						ret.add(root.offset(-d, y, 3));
						break;
					case 3:
						ret.add(root.offset(-3, y, -d));
						break;
				}
			}
		}
		return ret;
	}

	private HashMap<Coordinate, ForgeDirection> getSwitchDoorLocations(Coordinate root, int i) {
		HashMap<Coordinate, ForgeDirection> ret = new HashMap();
		for (int y = 6; y <= 8; y++) {
			for (int d = 5; d <= 6; d++) {
				switch(i) {
					case 0: //nw
						ret.put(root.offset(-2, y, -d), ForgeDirection.EAST);
						ret.put(root.offset(-d, y, -2), ForgeDirection.SOUTH);
						break;
					case 1: //ne
						ret.put(root.offset(-2, y, d), ForgeDirection.SOUTH);
						ret.put(root.offset(-d, y, 2), ForgeDirection.WEST);
						break;
					case 2: //sw
						ret.put(root.offset(2, y, -d), ForgeDirection.EAST);
						ret.put(root.offset(d, y, -2), ForgeDirection.NORTH);
						break;
					case 3: //se
						ret.put(root.offset(2, y, d), ForgeDirection.WEST);
						ret.put(root.offset(d, y, 2), ForgeDirection.NORTH);
						break;
				}
			}
		}
		return ret;
	}

	private HashMap<Coordinate, CrystalElement> getCrystalLocations(Coordinate root/*, int i*/) {
		HashMap<Coordinate, CrystalElement> ret = new HashMap();
		//switch(i) {
		//	case 0: //W
		ret.put(root.offset(-4, 3, 1), crystalColors.get(ret.size()));
		ret.put(root.offset(-5, 3, 1), crystalColors.get(ret.size()));
		//		break;
		//	case 1: //N
		ret.put(root.offset(-1, 3, -4), crystalColors.get(ret.size()));
		ret.put(root.offset(-1, 3, -5), crystalColors.get(ret.size()));
		//		break;
		//	case 2: //E
		ret.put(root.offset(4, 3, -1), crystalColors.get(ret.size()));
		ret.put(root.offset(5, 3, -1), crystalColors.get(ret.size()));
		//		break;
		//	case 3: //S
		ret.put(root.offset(1, 3, 4), crystalColors.get(ret.size()));
		ret.put(root.offset(1, 3, 5), crystalColors.get(ret.size()));
		//		break;
		//}
		return ret;
	}

	private Collection<Coordinate> getLiquidLocations(Coordinate root) {
		ArrayList<Coordinate> ret = new ArrayList();
		for (int a = -1; a <= 1; a++) {
			for (int b = -1; b <= 1; b++) {
				ret.add(root.offset(a, -1, b));
			}
		}
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			for (int a = 3; a <= 5; a++) {
				for (int b = 4; b <= 5; b++) {
					if (b == 5 && a == 4)
						continue;
					ret.add(root.offset(dir.offsetX*b+left.offsetX*a, 1, left.offsetZ*a+dir.offsetZ*b));
				}
			}
		}
		return ret;
	}

	private Collection<Coordinate> getLowerChestLocations(Coordinate root) {
		ArrayList<Coordinate> ret = new ArrayList();
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			ret.add(root.offset(dir.offsetX*5+left.offsetX*4, 2, left.offsetZ*4+dir.offsetZ*5));
		}
		return ret;
	}

	private Collection<Coordinate> getBarrierLocations(Coordinate root) {
		ArrayList<Coordinate> ret = new ArrayList();
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				ret.add(root.offset(x, 1, z));
			}
		}
		for (int y = 2; y <= 4; y++) {
			for (int d = 4; d <= 5; d++) {
				ret.add(root.offset(d, y, 3));
				ret.add(root.offset(-3, y, d));
				ret.add(root.offset(-d, y, -3));
				ret.add(root.offset(3, y, -d));
			}
		}
		return ret;
	}

	@Override
	public void onTick(TileEntityStructControl te) {
		if (te.getTicksExisted() == 5 || te.getTicksExisted()%200 == 0) {
			this.updateColorDoors(te.worldObj, new Coordinate(te));
		}
		EntityPlayer ep = te.worldObj.getClosestPlayer(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, 20);
		if (ep == null)
			return;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te).expand(1, 0, 1).offset(0, 6, 0);
		List<EntityPlayer> li = te.worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
		for (EntityPlayer ep2 : li) {
			if (!ProgressionManager.instance.playerHasPrerequisites(ep2, ProgressStage.BIOMESTRUCT))
				this.pushPlayer(ep2, te);
		}
		if (this.isPlayingMelody(ep)) {
			musicTick++;
			if (musicTick >= nextNoteTick) {
				this.playNextNote(te);
			}
		}
	}

	private boolean isPlayingMelody(EntityPlayer ep) {
		return ChromaItems.PROBE.matchWith(ep.getCurrentEquippedItem());//isPlayingMelody;
	}

	private void pushPlayer(EntityPlayer ep, TileEntityStructControl te) {
		/*
		double dx = ep.posX-te.xCoord-0.5;
		double dz = ep.posZ-te.zCoord-0.5;
		double v = 4.5;
		double dd = ReikaMathLibrary.py3d(dx, 0, dz);
		ep.addVelocity(v*dx/dd, 0, v*dz/dd);
		 */
		ep.motionY = Math.max(0, ep.motionY);
		ep.velocityChanged = true;
	}

	private void playNextNote(TileEntityStructControl te) {
		MusicKey key = melody.get(melodyIndex);
		ChromaSounds.DING.playSoundAtBlock(te, 1, (float)CrystalMusicManager.instance.getPitchFactor(key));

		ArrayList<CrystalElement> c = new ArrayList(CrystalMusicManager.instance.getColorsWithKey(key));
		for (CrystalElement e : c) {
			double s = 4.5-0.5*(c.size()-1);
			int[] d1 = ReikaJavaLibrary.splitDoubleToInts(s);
			int[] d2 = ReikaJavaLibrary.splitDoubleToInts(0);
			double dd = s/6D;
			double dx = ReikaRandomHelper.getRandomPlusMinus(te.xCoord+0.5, dd);
			double dy = ReikaRandomHelper.getRandomPlusMinus(te.yCoord+3.5, dd);
			double dz = ReikaRandomHelper.getRandomPlusMinus(te.zCoord+0.5, dd);
			ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.RUNEPARTICLE.ordinal(), te.worldObj, dx, dy, dz, 32, e.ordinal(), d1[0], d1[1], d2[0], d2[1], 6);
		}

		nextNoteTick += 8;
		melodyIndex++;
		if (melodyIndex >= melody.size()) {
			melodyIndex = 0;
			//nextNoteTick = 0;
			//isPlayingMelody = false;

			nextNoteTick += 16;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render() {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (!ProgressionManager.instance.playerHasPrerequisites(ep, ProgressStage.BIOMESTRUCT)) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDepthMask(false);

			BlendMode.MULTIPLY.apply();

			Tessellator var5 = Tessellator.instance;
			var5.startDrawingQuads();
			var5.setBrightness(240);
			var5.setColorOpaque_I(0xffffff);

			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = ChromaIcons.X.getIcon();
			float u1 = ico.getMinU();
			float v1 = ico.getMinV();
			float du1 = ico.getMaxU();
			float dv1 = ico.getMaxV();

			var5.addVertexWithUV(-1, 6.75, -1, u1, v1);
			var5.addVertexWithUV(2, 6.75, -1, du1, v1);
			var5.addVertexWithUV(2, 6.75, 2, du1, dv1);
			var5.addVertexWithUV(-1, 6.75, 2, u1, dv1);

			var5.draw();

			BlendMode.DEFAULT.apply();

			var5.startDrawingQuads();
			var5.setBrightness(240);
			var5.setColorRGBA_I(0xffffff, 64);

			u1 = ico.getMinU();
			v1 = ico.getMinV();
			du1 = ico.getMaxU();
			dv1 = ico.getMaxV();

			var5.addVertexWithUV(-1, 6.75, -1, u1, v1);
			var5.addVertexWithUV(2, 6.75, -1, du1, v1);
			var5.addVertexWithUV(2, 6.75, 2, du1, dv1);
			var5.addVertexWithUV(-1, 6.75, 2, u1, dv1);

			var5.draw();

			BlendMode.ADDITIVEDARK.apply();

			var5.startDrawingQuads();
			var5.setBrightness(240);
			var5.setColorOpaque_I(0x300000);

			ico = ChromaIcons.HIVE.getIcon();
			u1 = ico.getMinU();
			v1 = ico.getMinV();
			du1 = ico.getMaxU();
			dv1 = ico.getMaxV();

			var5.addVertexWithUV(-1, 6, -1, u1, v1);
			var5.addVertexWithUV(2, 6, -1, du1, v1);
			var5.addVertexWithUV(2, 6, 2, du1, dv1);
			var5.addVertexWithUV(-1, 6, 2, u1, dv1);
			var5.addVertexWithUV(-1, 7, -1, u1, v1);
			var5.addVertexWithUV(2, 7, -1, du1, v1);
			var5.addVertexWithUV(2, 7, 2, du1, dv1);
			var5.addVertexWithUV(-1, 7, 2, u1, dv1);

			var5.setColorOpaque_I(0xff0000);
			ico = ChromaIcons.RIFT.getIcon();
			u1 = ico.getMinU();
			v1 = ico.getMinV();
			du1 = ico.getMaxU();
			dv1 = ico.getMaxV();

			var5.addVertexWithUV(-1, 6.5, -1, u1, v1);
			var5.addVertexWithUV(2, 6.5, -1, du1, v1);
			var5.addVertexWithUV(2, 6.5, 2, du1, dv1);
			var5.addVertexWithUV(-1, 6.5, 2, u1, dv1);

			var5.draw();

			ReikaTextureHelper.bindEnchantmentTexture();
			double r = 12;
			int color = ReikaColorAPI.mixColors(0xff0000, 0x700000, (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/100D)));
			var5.startDrawingQuads();
			var5.setBrightness(240);
			var5.setColorOpaque_I(color);
			double dx = 0.5;
			double dy = 5.5;
			double dz = 0.5;
			double dk = 0.5*r/16;
			double di = 10;
			for (double k = -r; k <= r-dk; k += dk) {
				double dr = r*(1-0.75*Math.pow(k/r, 2));
				double dr2 = r*(1-0.75*Math.pow((k+dk)/r, 2));
				double r2 = Math.abs(k) >= r ? 0 : Math.sqrt(Math.max(0, dr*dr-k*k));
				double r3 = Math.abs(k) >= r ? 0 : Math.sqrt(Math.max(0, dr2*dr2-(k+dk)*(k+dk)));
				if (Double.isNaN(r2) || Double.isNaN(r3))
					continue;
				for (int i = 0; i < 360; i += di) {
					double a = Math.toRadians(i);
					double a2 = Math.toRadians(i+di);
					double ti = i+(System.currentTimeMillis()/50D%360);
					double tk = k+(System.currentTimeMillis()/220D%360);
					double u = ti/360D*3;
					double du = (ti+di)/360D*3;
					double v = tk*r/1024D;
					double dv = (tk+dk)*r/1024D;
					double s1 = Math.sin(a);
					double s2 = Math.sin(a2);
					double c1 = Math.cos(a);
					double c2 = Math.cos(a2);
					var5.addVertexWithUV(dx+r2*c1, dy+k, dz+r2*s1, u, v);
					var5.addVertexWithUV(dx+r2*c2, dy+k, dz+r2*s2, du, v);
					var5.addVertexWithUV(dx+r3*c2, dy+k+dk, dz+r3*s2, du, dv);
					var5.addVertexWithUV(dx+r3*c1, dy+k+dk, dz+r3*s1, u, dv);
				}
			}
			var5.draw();
		}
	}

}
