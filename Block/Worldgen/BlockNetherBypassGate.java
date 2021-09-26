package Reika.ChromatiCraft.Block.Worldgen;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemNetherKey;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;


public class BlockNetherBypassGate extends BlockAir {

	public static enum GateLevels {
		PUSH1,
		PUSH2,
		PUSH3,
		HURT1,
		HURT2,
		KILL;

		public static final GateLevels[] list = values();

		public void doEffect(EntityPlayer ep) {
			//ReikaJavaLibrary.pConsole(this);
			switch(this) {
				case PUSH1:
					if (ep.motionY > 0)
						ep.motionY *= 0.95;
					ep.motionY -= 0.125F;
					ep.velocityChanged = true;
					break;
				case PUSH2:
					if (ep.motionY > 0)
						ep.motionY *= 0.9;
					ep.motionY -= 0.25F;
					ep.velocityChanged = true;
					break;
				case PUSH3:
					if (ep.motionY > 0)
						ep.motionY *= 0.8;
					ep.motionY -= 0.5F;
					ep.velocityChanged = true;
					break;
				case HURT1:
					ReikaEntityHelper.doSetHealthDamage(ep, DamageSource.outOfWorld, 0.25F);
					PUSH3.doEffect(ep);
					break;
				case HURT2:
					ReikaEntityHelper.doSetHealthDamage(ep, DamageSource.outOfWorld, 1F);
					PUSH3.doEffect(ep);
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

		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(6000000);
		this.setBlockUnbreakable();
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityPlayer && e.isEntityAlive() && e.getEntityData().getLong("lastGateEffect") < world.getTotalWorldTime()) {
			EntityPlayer ep = (EntityPlayer)e;
			if (!ep.capabilities.isCreativeMode && !ItemNetherKey.isPlayerTagged(ep)) {
				GateLevels.list[world.getBlockMetadata(x, y, z)].doEffect(ep);
				ep.getEntityData().setLong("lastGateEffect", world.getTotalWorldTime());
			}
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

}
