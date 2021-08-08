package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Items.Tools.Powered.ItemNetherKey;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class BlockNetherBypassGate extends BlockAir {

	public static enum GateLevels {
		PUSH1,
		PUSH2,
		PUSH3,
		HURT1,
		HURT2,
		KILL;

		private static final GateLevels[] list = values();

		public void doEffect(EntityPlayer ep) {
			ReikaJavaLibrary.pConsole(this);
			switch(this) {
				case PUSH1:
					ep.motionY -= 0.1F;
					ep.velocityChanged = true;
					break;
				case PUSH2:
					ep.motionY -= 0.3F;
					ep.velocityChanged = true;
					break;
				case PUSH3:
					ep.motionY -= 0.75F;
					ep.velocityChanged = true;
					break;
				case HURT1:
					ReikaEntityHelper.doSetHealthDamage(ep, DamageSource.outOfWorld, 0.25F);
					break;
				case HURT2:
					ReikaEntityHelper.doSetHealthDamage(ep, DamageSource.outOfWorld, 1F);
					break;
				case KILL:
					ReikaEntityHelper.doSetHealthDamage(ep, DamageSource.outOfWorld, Integer.MAX_VALUE);
					break;
				default:
					break;
			}
		}
	}

	public BlockNetherBypassGate() {
		super();
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityPlayer && e.isEntityAlive() && e.getEntityData().getLong("lastGateEffect") < world.getTotalWorldTime()) {
			EntityPlayer ep = (EntityPlayer)e;
			if (!ItemNetherKey.isPlayerTagged(ep)) {
				GateLevels.list[world.getBlockMetadata(x, y, z)].doEffect(ep);
				ep.getEntityData().setLong("lastGateEffect", world.getTotalWorldTime());
			}
		}
	}

}
