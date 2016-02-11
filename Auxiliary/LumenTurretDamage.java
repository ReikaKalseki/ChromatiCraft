package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLumenTurret;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;


public class LumenTurretDamage extends CustomStringDamageSource {

	private final EntityPlayer player;

	public LumenTurretDamage(TileEntityLumenTurret te) {
		super("got too close to "+te.getPlacerName()+"'s "+te.getName());
		player = te.getPlacer();
	}

	@Override
	public Entity getSourceOfDamage()
	{
		return player;
	}

	@Override
	public boolean isMagicDamage()
	{
		return true;
	}

}
