package Reika.ChromatiCraft.World;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;


public class GenLootCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);

		if (args.length == 1) {
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
			if (mov != null) {
				TileEntity te = ep.worldObj.getTileEntity(mov.blockX, mov.blockY, mov.blockZ);
				if (te instanceof IInventory) {
					WeightedRandomChestContent[] types = ChestGenHooks.getItems(args[0], ep.getRNG());
					if (types.length == 0) {
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Loot table "+args[0]+" is empty.");
					}
					else {
						int count = ChestGenHooks.getCount(args[0], ep.getRNG());
						WeightedRandomChestContent.generateChestContents(ep.getRNG(), types, (IInventory)te, count);
						this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+args[0]+" in chest @ "+new Coordinate(te));
					}
				}
				else {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"TileEntity at "+mov.blockX+", "+mov.blockY+", "+mov.blockZ+" is not an inventory.");
				}
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"No selected inventory.");
			}
		}
		else {
			try {
				Field f = ChestGenHooks.class.getDeclaredField("chestInfo");
				f.setAccessible(true);
				HashMap<String, ChestGenHooks> map = (HashMap<String, ChestGenHooks>)f.get(null);
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify a loot table.");
				this.sendChatToSender(ics, "Valid types: "+map.keySet());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getCommandString() {
		return "genloot";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
