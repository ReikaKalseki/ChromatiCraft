/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import Reika.ChromatiCraft.World.Dimension.BiomeTerrainProvider;
import Reika.ChromatiCraft.World.Dimension.BiomeTerrainProvider.ForcedBiomeTerrainProvider;
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
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class DimensionGeneratorCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			ChromaDimensionBiomeType b = Biomes.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			int r = 1;
			if (args.length >= 2 && Boolean.parseBoolean(args[1])) {
				b = ((Biomes)b).getSubBiome();
				if (args.length == 3) {
					r = ReikaJavaLibrary.safeIntParse(args[2]);
				}
			}
			else if (args.length == 2) {
				r = ReikaJavaLibrary.safeIntParse(args[1]);
			}
			int x = MathHelper.floor_double(ep.posX)/16;
			int z = MathHelper.floor_double(ep.posZ)/16;
			ReikaChatHelper.sendChatToPlayer(ep, "Running generators on "+(r*2+1)+"x"+(r*2+1)+" chunks...");
			Collection<ChromaWorldGenerator> gen = new ArrayList();
			for (int i = 0; i < DimensionGenerators.generators.length; i++) {
				DimensionGenerators g = DimensionGenerators.generators[i];
				if (g.generateIn(b.getBiome())) {
					gen.add(g.getGenerator(ep.worldObj.rand, ep.worldObj.getSeed()));
				}
			}
			BiomeTerrainProvider terrain = new ForcedBiomeTerrainProvider(b, ep.worldObj.getSeed());
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					terrain.generateChunk(ep.worldObj, x+i, z+k, ep.worldObj.rand);
				}
			}
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
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.RERENDER.ordinal(), new PacketTarget.PlayerTarget((EntityPlayerMP)ep), 0);
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
