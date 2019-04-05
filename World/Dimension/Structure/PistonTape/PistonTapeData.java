package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructureDataStorage;


public class PistonTapeData extends StructureData {

	static final int STEP_DURATION = 80;

	private final ArrayList<TapeStage> steps;

	private int index = 0;
	private boolean isPlaying = false;
	private int ticksUntilStep = STEP_DURATION;

	public PistonTapeData(DimensionStructureGenerator gen, ArrayList<TapeStage> doors) {
		super(gen);

		steps = doors;
	}

	@Override
	public void load(HashMap<String, Object> map) {

	}

	@Override
	public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {
		isPlaying = true;
	}

	@Override
	public void onTileTick(TileEntityStructureDataStorage te) {
		if (isPlaying) {
			if (ticksUntilStep > 0) {
				ticksUntilStep--;
			}
			else {
				this.step(te.worldObj);
			}
			steps.get(index).tick(te.worldObj);
		}
	}

	private void step(World world) {
		ticksUntilStep = STEP_DURATION;
		index++;
		//((PistonTapeGenerator)generator).setDoorState(steps.get(index));
		for (int i = 0; i < steps.size(); i++) {
			steps.get(i).setActive(world, i == index);
		}
		if (index == 0) {
			isPlaying = false;
			steps.get(0).setActive(world, false);
		}
	}

}
