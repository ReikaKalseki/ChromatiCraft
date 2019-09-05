package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructureDataStorage;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;


public class PistonTapeData extends StructureData {

	public static final int STEP_DURATION = 80;

	private boolean active = false;
	private int tick = 0;

	public PistonTapeData(DimensionStructureGenerator gen) {
		super(gen);
	}

	@Override
	public void load(HashMap<String, Object> map) {

	}

	@Override
	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {
		active = true;
		tick = 0;
	}

	@Override
	public void onTileTick(TileEntityStructureDataStorage te) {
		if (active) {
			int d = 120;
			TapeStage s = ((PistonTapeGenerator)generator).getStage(0);
			int max = s.doorCount;
			int t2 = tick/d;
			if (t2 >= max) {
				active = false;
				tick = 0;
			}
			else {
				if (tick%d == 0) {
					//ReikaJavaLibrary.pConsole(t2);
					s.fireEmitters(te.worldObj, t2);
				}
				tick++;
			}
		}
	}
}
