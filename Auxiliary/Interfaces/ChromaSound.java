package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.Registry.DynamicSound;

public interface ChromaSound extends DynamicSound {

	public void playSoundAtBlock(TileEntity te, float vol, float pitch);
	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch);
	public void playSoundAtBlockNoAttenuation(TileEntity te, float vol, float pitch, int range);
	public boolean hasWiderPitchRange();
	public ChromaSound getUpshiftedPitch();
	public ChromaSound getDownshiftedPitch();
	public float getRangeInterval();

}
