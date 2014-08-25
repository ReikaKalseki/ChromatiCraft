/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.net.URL;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Interfaces.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public enum ChromaSounds implements SoundEnum {

	RIFT("rift", SoundCategory.BLOCKS),
	POWERDOWN("powerdown-2", SoundCategory.BLOCKS),
	DISCHARGE("discharge", SoundCategory.BLOCKS),
	CAST("cast3", SoundCategory.BLOCKS),
	POWER("ambient", SoundCategory.AMBIENT),
	CRAFTING("ambient1_short", SoundCategory.MASTER),
	CRAFTDONE("craftdone", SoundCategory.BLOCKS),
	UPGRADE("upgrade", SoundCategory.BLOCKS);

	public static final ChromaSounds[] soundList = values();

	public static final String PREFIX = "Reika/ChromatiCraft/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ChromatiCraft.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";
	private static final String MUSIC_FOLDER = "music/";
	private static final String MUSIC_PREFIX = "music.";

	private final String path;
	private final String name;
	private final SoundCategory category;

	private boolean isVolumed = false;

	private ChromaSounds(String n, SoundCategory cat) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		name = n;
		path = PREFIX+SOUND_FOLDER+name+SOUND_EXT;
		category = cat;
	}

	public float getSoundVolume() {
		float vol = 1;//ConfigRegistry.MACHINEVOLUME.getFloat(); //config float
		if (vol < 0)
			vol = 0;
		if (vol > 1)
			vol = 1F;
		return vol;
	}

	public float getModVolume() {
		if (!isVolumed)
			return 1F;
		else
			return this.getSoundVolume();
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
			return;
		ReikaPacketHelper.sendSoundPacket(ChromatiCraft.packetChannel, this, world, x, y, z, vol*this.getModVolume(), pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	public void playSoundAtBlock(TileEntity te) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public void playSoundAtBlock(WorldLocation loc) {
		this.playSoundAtBlock(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public String getName() {
		return this.name();
	}

	public String getPath() {
		return path;
	}

	public URL getURL() {
		return ChromatiCraft.class.getResource(SOUND_DIR+name+SOUND_EXT);
	}

	public static ChromaSounds getSoundByName(String name) {
		for (int i = 0; i < soundList.length; i++) {
			if (soundList[i].name().equals(name))
				return soundList[i];
		}
		ReikaJavaLibrary.pConsole("\""+name+"\" does not correspond to a registered sound!");
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		return category;
	}
}
