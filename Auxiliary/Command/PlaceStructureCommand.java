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

import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterNetherStructure;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class PlaceStructureCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		ChromaStructures s = ChromaStructures.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		s.getStructure().resetToDefaults();
		if (s == ChromaStructures.NETHERTRAP)
			((VoidMonsterNetherStructure)s.getStructure()).setTNT(args.length == 1 || Boolean.parseBoolean(args[1]));
		CrystalElement e = s.requiresColor ? CrystalElement.valueOf(args[1].toUpperCase(Locale.ENGLISH)) : null;
		FilledBlockArray arr = s.getArray(ep.worldObj, x, y, z, e);
		arr.place();
		s.getStructure().resetToDefaults();
		if (s.getStructure() instanceof FragmentStructureBase) {
			Coordinate c = ((FragmentStructureBase)s.getStructure()).getControllerRelativeLocation().offset(x, y, z);
			c.setBlock(ep.worldObj, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata());
			TileEntityStructControl te = (TileEntityStructControl)c.getTileEntity(ep.worldObj);
			te.generate(s, e != null ? e : CrystalElement.WHITE);
			DungeonGenerator.instance.populateChests(s, arr, ep.getRNG());
		}
	}

	@Override
	public String getCommandString() {
		return "chromastruct";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
