/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.net.URL;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public enum ChromaSounds implements SoundEnum {

	RIFT("rift"),
	POWERDOWN("powerdown-2"),
	DISCHARGE("discharge"),
	CAST("cast3"),
	POWER("ambient"),
	CRAFTING("ambient1_short"),
	CRAFTING_BOOST("ambient1_short_boost"),
	CRAFTDONE("craftdone2"),
	UPGRADE("upgrade"),
	ABILITY("ability"),
	ERROR("error"),
	INFUSE("infuse"),
	INFUSION("infuse2"),
	USE("use2"),
	TRAP("slam2"),
	DING("ding2"),
	DING_HI("ding2_hi"),
	DING_LO("ding2_lo"),
	SHOCKWAVE("shockwave3"),
	BALLLIGHTNING("balllightning"),
	ITEMSTAND("stand"),
	POWERCRYS("powercrystal"),
	GUICLICK("gui2"),
	GUISEL("gui4"),
	DRONE("drone2"),
	PORTAL("portal2"),
	ORB("orb"),
	ORB_HI("orb_hi"),
	ORB_LO("orb_lo"),
	GOTODIM("todim"),
	OVERLOAD("discharge2"),
	PYLONFLASH("pylonboost"),
	PYLONTURBO("pylonturbo"),
	PYLONBOOSTRITUAL("pylonboost_ritual_short"),
	PYLONBOOSTSTART("pylonbooststart"),
	DASH("dash"),
	REPEATERSURGE("repeatersurge"),
	FIRE("fire"),
	LASER("laser"),
	MONUMENT("monument"),
	MONUMENTRAY("monumentray"),
	MONUMENTCOMPLETE("monumentcomplete"),
	BUFFERWARNING("buffer_warning"),
	BUFFERWARNING_LOW("buffer_warning2"),
	BUFFERWARNING_EMPTY("buffer_warning3"),
	KILLAURA("killaura"),
	KILLAURA_CHARGE("killaura_charge"),
	POWERCRAFT("powercraft"),
	METEOR("meteor"),
	IMPACT("impact"),
	NOCLIPON("rumble-in"),
	NOCLIPOFF("rumble-out"),
	NOCLIPRUN("rumble"),
	FLAREATTACK("flareattack"),
	BOUNCE("bounce"),
	SKYRIVER("lumenstream"),
	PING("ping"),
	GAINPROGRESS("progress"),
	AVOLASER("avolaser2"),
	CLIFFSOUND("cliffambience"),
	CLIFFSOUND2("cliffambience2"),
	INSCRIBE("inscribe"),
	;

	public static final ChromaSounds[] soundList = values();

	public static final String PREFIX = "Reika/ChromatiCraft/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ChromatiCraft.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";

	private final String path;
	private final String name;
	//private final SoundCategory category;

	private boolean isVolumed = false;

	private ChromaSounds(String n) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		name = n;
		path = PREFIX+SOUND_FOLDER+name+SOUND_EXT;
		//category = cat;
	}

	public float getSoundVolume() {
		float vol = 1;
		if (vol < 0)
			vol = 0;
		if (vol > 1)
			vol = 1F;
		return vol;
	}

	@Override
	public float getModulatedVolume() {
		if (!isVolumed)
			return 1F;
		else
			return this.getSoundVolume();
	}

	public void playSound(Entity e) {
		this.playSound(e, 1, 1);
	}

	public void playSound(Entity e, float vol, float pitch) {
		this.playSound(e.worldObj, e.posX, e.posY, e.posZ, vol, pitch);
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch) {
		if (world.isRemote)
			return;
		ReikaSoundHelper.playSound(this, ChromatiCraft.packetChannel, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z, float vol, float pitch) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	public void playSoundAtBlock(World world, int x, int y, int z) {
		this.playSound(world, x+0.5, y+0.5, z+0.5, 1, 1);
	}

	public void playSoundAtBlock(TileEntity te, float vol, float pitch) {
		this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord, vol, pitch);
	}

	public void playSoundAtBlockNoAttenuation(TileEntity te, float vol, float pitch, int broadcast) {
		this.playSoundAtBlockNoAttenuation(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, vol, pitch, broadcast);
	}

	public void playSoundAtBlockNoAttenuation(World world, double x, double y, double z, float vol, float pitch, int broadcast) {
		if (world.isRemote)
			return;
		//ReikaSoundHelper.playSound(this, ChromatiCraft.packetChannel, te.worldObj, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, false);
		ReikaPacketHelper.sendSoundPacket(ChromatiCraft.packetChannel, this, world, x, y, z, vol, pitch, false, broadcast);
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
		ChromatiCraft.logger.logError("\""+name+"\" does not correspond to a registered sound!");
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.MASTER;
	}

	@Override
	public boolean canOverlap() {
		return this == RIFT || this == CAST || this == USE || this == ERROR || this == INFUSE || this == DING || this == DRONE || this == ITEMSTAND || this == KILLAURA_CHARGE;
	}

	@Override
	public boolean attenuate() {
		return this != GOTODIM && this != PYLONTURBO && this != PYLONFLASH && this != PYLONBOOSTRITUAL && this != PYLONBOOSTSTART && this != REPEATERSURGE && this != MONUMENT && this != MONUMENTRAY;
	}

	public boolean hasWiderPitchRange() {
		return this == DING || this == ORB;
	}

	public ChromaSounds getUpshiftedPitch() {
		if (this == DING)
			return DING_HI;
		if (this == ORB)
			return ORB_HI;
		return this;
	}

	public ChromaSounds getDownshiftedPitch() {
		if (this == DING)
			return DING_LO;
		if (this == ORB)
			return ORB_LO;
		return this;
	}

	@Override
	public boolean preload() {
		switch(this) {
			case MONUMENT:
			case MONUMENTCOMPLETE:
			case CRAFTING:
			case CRAFTING_BOOST:
			case POWERCRAFT:
			case INFUSION:
			case ABILITY:
			case GOTODIM:
			case PYLONBOOSTRITUAL:
			case REPEATERSURGE:
				return true;
			default:
				return false;
		}
	}
}
