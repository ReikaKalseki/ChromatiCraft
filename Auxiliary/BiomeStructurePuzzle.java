package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightSwitch;
import Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel.BlockLightSwitch.LightSwitchTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockLockKey.TileEntityLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.ShiftMaze.BlockShiftLock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.FragmentStructureData;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.ChromatiCraft.World.Dimension.Structure.MusicPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public class BiomeStructurePuzzle implements FragmentStructureData {

	private final ArrayList<MusicKey> melody = new ArrayList();
	private final SwitchGroup[] doorKeys = new SwitchGroup[4];
	private final ColorPair[] colors = new ColorPair[4];
	private final ArrayList<CrystalElement> crystalColors = new ArrayList();

	private final HashSet<SwitchGroup> usedKeys = new HashSet();
	private final HashSet<ColorPair> usedColors = new HashSet();

	private final HashMap<Coordinate, CrystalElement> runes = new HashMap();

	public void clear() {
		melody.clear();
		crystalColors.clear();
		runes.clear();
		usedColors.clear();
		usedKeys.clear();
	}

	public void generate(Random rand) {
		for (int i = 0; i < doorKeys.length; i++) {
			SwitchGroup key = SwitchGroup.random(rand);
			while (usedKeys.contains(key))
				key = SwitchGroup.random(rand);
			doorKeys[i] = key;
			usedKeys.add(key);
		}
		for (int i = 0; i < colors.length; i++) {
			ColorPair key = ColorPair.random(rand);
			while (usedColors.contains(key))
				key = ColorPair.random(rand);
			colors[i] = key;
			usedColors.add(key);
		}
		melody.addAll(MusicPuzzleGenerator.getRandomPrefab(rand, Integer.MAX_VALUE, null).getNotes());
		this.calculateCrystals(rand);
	}

	private void calculateCrystals(Random rand) {
		HashSet<MusicKey> needed = new HashSet(melody);
		while (!needed.isEmpty()) {
			CrystalElement e = this.findMostEffective(needed);
			crystalColors.add(e);
			needed.removeAll(CrystalMusicManager.instance.getKeys(e));
		}
		if (crystalColors.size() > 8) {
			throw new RuntimeException(melody+" > "+crystalColors);
		}
		while (crystalColors.size() < 8) {
			CrystalElement e = CrystalElement.elements[rand.nextInt(16)];
			while (crystalColors.contains(e))
				e = CrystalElement.elements[rand.nextInt(16)];
			crystalColors.add(e);
		}
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

	private static class ColorPair {

		private final CrystalElement color1;
		private final CrystalElement color2;

		private ColorPair(int c1, int c2) {
			this(CrystalElement.elements[c1], CrystalElement.elements[c2]);
		}

		private ColorPair(CrystalElement e1, CrystalElement e2) {
			color1 = e1;
			color2 = e2;
		}

		private static ColorPair random(Random rand) {
			CrystalElement e1 = CrystalElement.elements[rand.nextInt(16)];
			CrystalElement e2 = CrystalElement.elements[rand.nextInt(16)];
			while (e1 == e2)
				e2 = CrystalElement.elements[rand.nextInt(16)];
			return new ColorPair(e1, e2);
		}

		@Override
		public int hashCode() {
			return color1.ordinal() | (color2.ordinal() << 8);
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof ColorPair && this.matchKeys((ColorPair)o);
		}

		private boolean matchKeys(ColorPair o) {
			return o.color1 == color1 && o.color2 == color2;
		}

		private NBTTagCompound writeToTag() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("c1", color1.ordinal());
			tag.setInteger("c2", color2.ordinal());
			return tag;
		}

		private static ColorPair readTag(NBTTagCompound tag) {
			return new ColorPair(tag.getInteger("c1"), tag.getInteger("c2"));
		}

	}

	private static class SwitchGroup {

		private final boolean NW;
		private final boolean SW;
		private final boolean NE;
		private final boolean SE;

		private SwitchGroup(boolean nw, boolean sw, boolean ne, boolean se) {
			NW = nw;
			SW = sw;
			NE = ne;
			SE = se;
		}

		private static SwitchGroup random(Random rand) {
			return new SwitchGroup(rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean());
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
			return tag;
		}

		private static SwitchGroup readTag(NBTTagCompound tag) {
			return new SwitchGroup(tag.getBoolean("nw"), tag.getBoolean("sw"), tag.getBoolean("ne"), tag.getBoolean("se"));
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
		for (Entry<Coordinate, CrystalElement> e : runes.entrySet()) {
			NBTTagCompound tag = e.getKey().writeToTag();
			tag.setInteger("color", e.getValue().ordinal());
			li.appendTag(tag);
		}
		NBT.setTag("runes", li);

		for (int i = 0; i < doorKeys.length; i++) {
			NBT.setTag("door"+i, doorKeys[i].writeToTag());
		}
		for (int i = 0; i < colors.length; i++) {
			NBT.setTag("color"+i, colors[i].writeToTag());
		}
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

		li = NBT.getTagList("runes", NBTTypes.INT.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			Coordinate c = Coordinate.readTag(tag);
			CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
			runes.put(c, e);
		}

		for (int i = 0; i < doorKeys.length; i++) {
			doorKeys[i] = SwitchGroup.readTag(NBT.getCompoundTag("door"+i));
		}
		for (int i = 0; i < colors.length; i++) {
			colors[i] = ColorPair.readTag(NBT.getCompoundTag("color"+i));
		}
	}

	@Override
	public void handleTileAdd(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root) {
		if (te instanceof TileEntityLockKey) {
			this.updateColorDoors(world, root);
		}
	}

	@Override
	public void handleTileRemove(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root) {
		if (te instanceof TileEntityLockKey) {
			this.updateColorDoors(world, root);
		}
	}

	@Override
	public void handleTileInteract(World world, int x, int y, int z, InteractionDelegateTile te, TileEntityStructControl root) {
		if (te instanceof LightSwitchTile) {
			this.updateSwitchDoors(world, root);
		}
	}

	private void updateColorDoors(World world, TileEntityStructControl root) {
		HashSet<CrystalElement> opened = new HashSet();
		for (Entry<Coordinate, CrystalElement> e : this.getRuneLocations(root)) {
			Coordinate c = e.getKey().offset(0, 1, 0);
			if (c.getBlock(world) == ChromaBlocks.LOCKKEY.getBlockInstance()) {
				opened.add(e.getValue());
			}
		}
		for (Coordinate c : this.getColorDoorLocations(root)) {
			TileEntityColorLock te = (TileEntityColorLock)c.getTileEntity(world);
			te.setOpenColors(opened);
		}
	}

	private void updateSwitchDoors(World world, TileEntityStructControl root) {
		for (int i = 0; i < 4; i++) {
			boolean flag = doorKeys[i].validate(world, this.switchNW(root), this.switchSW(root), this.switchNE(root), this.switchSE(root));
			for (Coordinate c : this.getSwitchDoorLocations(root, i)) {
				BlockShiftLock.setOpen(world, c.xCoord, c.yCoord, c.zCoord, flag);
			}
		}
	}

	private void openBarriers(World world, TileEntityStructControl root) {
		for (Coordinate c : this.getBarrierLocations(root)) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, true);
		}
	}

	private Coordinate switchNW(TileEntityStructControl root) {

	}

	private Coordinate switchSW(TileEntityStructControl root) {

	}

	private Coordinate switchNE(TileEntityStructControl root) {

	}

	private Coordinate switchSE(TileEntityStructControl root) {

	}

	private Set<Entry<Coordinate, CrystalElement>> getRuneLocations(TileEntityStructControl root) {

	}

	private Collection<Coordinate> getColorDoorLocations(TileEntityStructControl root) {

	}

	private Collection<Coordinate> getSwitchDoorLocations(TileEntityStructControl root, int i) {

	}

	private Collection<Coordinate> getBarrierLocations(TileEntityStructControl root) {

	}

}
