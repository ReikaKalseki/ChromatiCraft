/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class DimensionGeneratorCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			ChromaDimensionBiomeType b = Biomes.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			if (args.length == 2 && Boolean.parseBoolean(args[1])) {
				b = ((Biomes)b).getSubBiome();
			}
			int x = MathHelper.floor_double(ep.posX)/16;
			int z = MathHelper.floor_double(ep.posZ)/16;
			ReikaChatHelper.sendChatToPlayer(ep, "Running generators");
			Collection<ChromaWorldGenerator> gen = new ArrayList();
			for (int i = 0; i < DimensionGenerators.generators.length; i++) {
				DimensionGenerators g = DimensionGenerators.generators[i];
				if (g.generateIn(b.getBiome())) {
					gen.add(g.getGenerator());
				}
			}
			int r = 4;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = (x+i)*16;
					int dz = (z+k)*16;
					for (ChromaWorldGenerator g : gen) {
						float f = g.getGenerationChance(ep.worldObj, dx, dz, b.getBiome());
						int n = (int)f;
						if (ReikaRandomHelper.doWithChance(f-n))
							n++;
						for (int a = 0; a < n; a++) {
							int gx = dx + ep.worldObj.rand.nextInt(16) + 8;
							int gz = dz + ep.worldObj.rand.nextInt(16) + 8;
							int gy = ep.worldObj.getTopSolidOrLiquidBlock(gx, gz);
							g.generate(ep.worldObj, ep.worldObj.rand, gx, gy, gz);
						}
					}
				}
			}
			ReikaPacketHelper.sendUpdatePacket(DragonAPIInit.packetChannel, PacketIDs.RERENDER.ordinal(), 0, 0, 0, new PacketTarget.PlayerTarget((EntityPlayerMP)ep));
			ReikaChatHelper.sendChatToPlayer(ep, "Generation complete.");
		}
	}

	@Override
	public String getCommandString() {
		return "rundimgenerators";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}



}
