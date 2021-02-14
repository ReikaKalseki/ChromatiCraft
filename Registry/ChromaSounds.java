/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaSound;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.IO.SoundVariant;
import Reika.DragonAPI.Interfaces.Registry.DynamicSound;
import Reika.DragonAPI.Interfaces.Registry.VariableSound;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

public enum ChromaSounds implements ChromaSound, DynamicSound, VariableSound {

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
	INFUSION_SHORT("infuse2s"),
	USE("use2"),
	TRAP("slam2"),
	DING("ding2", true),
	SHOCKWAVE("shockwave3"),
	BALLLIGHTNING("balllightning"),
	ITEMSTAND("stand"),
	GLOWCLOUD("powercrystal"),
	GUICLICK("gui2"),
	GUISEL("gui4"),
	DRONE("drone2"),
	DRONE_HI("drone2_hi"),
	PORTAL("portal2"),
	ORB("orb/orb", true),
	GOTODIM("todim"),
	OVERLOAD("discharge2"),
	PYLONFLASH("pylonboost"),
	PYLONTURBO("pylonturbo"),
	PYLONBOOSTRITUAL("pylonboost_ritual_short"),
	PYLONBOOSTSTART("pylonbooststart"),
	DASH("dash"),
	REPEATERSURGE("repeatersurge"),
	REPEATERSURGE_WEAK("repeatersurge_weak"),
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
	PING("ping2"),
	GAINPROGRESS("progress2"),
	AVOLASER("avolaser2"),
	CLIFFSOUND("cliffambience"),
	CLIFFSOUND2("cliffambience2"),
	CLIFFSOUND3("cliffambience3"),
	CLIFFSOUND4("cliffambience4"),
	INSCRIBE("inscribe"),
	LOREHEX("lore"),
	LORECOMPLETE("lorecomplete2"),
	LIGHTCAST("lightcast"),
	WATERLOCK("waterlock"),
	REPEATERRING("repeaterring"),
	FAIL("fail"),
	DIMENSIONHUM("dimensionhum"),
	DIMENSIONHUM_HI("dimensionhum_hi"),
	RADIANCE("radiance2"),
	CASTHARMONIC("castingharmonic2"),
	ABILITYCOMPLETE("abilityfinish2"),
	NETWORKOPT("networkopt"),
	NETWORKOPTCHARGE("networkopt_charge2"),
	TUNNELNUKERAMBIENT("nukerfly2"),
	TUNNELNUKERCALL("nukercall"),
	TOWEREXTEND1("tower_extend_1"),
	TOWEREXTEND2("tower_extend_2b"),
	TOWERAMBIENT("tower_ambient2"),
	CASTTUNEREJECT("casttunereject"),
	DINGCHARGE("dingcharge"),
	ARTEALLOY("artealloy2b"),
	ARTEALLOYHIT("artealloy-hit2"),
	LOWAMBIENT("lowambient_fade"),
	LOWAMBIENT_SHORT("lowambient_fade_short"),
	FLUTE("flute/flute", false),
	;

	public static final ChromaSounds[] soundList = values();

	public static final String PREFIX = "Reika/ChromatiCraft/";
	public static final String SOUND_FOLDER = "Sounds/";
	private static final String SOUND_PREFIX = "Reika.ChromatiCraft.Sounds.";
	private static final String SOUND_DIR = "Sounds/";
	private static final String SOUND_EXT = ".ogg";

	private final String path;
	private final String relative;
	private final String folder;
	private final boolean widePitch;
	private final HashMap<String, SoundVariant> variants = new HashMap();
	//private final SoundCategory category;

	private boolean isVolumed = false;

	private ChromaSounds(String n) {
		this(n, false);
	}

	private ChromaSounds(String n, boolean wide) {
		if (n.startsWith("#")) {
			isVolumed = true;
			n = n.substring(1);
		}
		relative = n;
		String f = "";
		int idx = n.lastIndexOf('/');
		if (idx >= 0) {
			f = n.substring(0, idx);
		}
		folder = f;
		widePitch = wide;
		path = PREFIX+SOUND_FOLDER+n+SOUND_EXT;
		if (this.hasWiderPitchRange()) {
			this.createVariant("LO");
			this.createVariant("HI");
		}
		//category = cat;
	}

	private ChromaSoundVariant createVariant(String name) {
		ChromaSoundVariant var = new ChromaSoundVariant(this, name);
		variants.put(name, var);
		return var;
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
		ReikaSoundHelper.playSound(this, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch);
	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch, boolean attenuate) {
		if (world.isRemote)
			return;
		ReikaSoundHelper.playSound(this, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, attenuate);
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
		this.playSoundNoAttenuation(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, vol, pitch, broadcast);
	}

	public void playSoundNoAttenuation(World world, double x, double y, double z, float vol, float pitch, int broadcast) {
		if (world.isRemote)
			return;
		//ReikaSoundHelper.playSound(this, ChromatiCraft.packetChannel, te.worldObj, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, false);
		ReikaPacketHelper.sendSoundPacket(this, world, x, y, z, vol, pitch, false, broadcast);
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
		return ChromatiCraft.class.getResource(SOUND_DIR+relative+SOUND_EXT);
	}

	@Override
	public SoundCategory getCategory() {
		if (this == GLOWCLOUD || this == BALLLIGHTNING)
			return SoundCategory.MOBS;
		return SoundCategory.MASTER;
	}

	@Override
	public boolean canOverlap() {
		return this == RIFT || this == CAST || this == USE || this == ERROR || this == INFUSE || this == DING || this == DRONE || this == ITEMSTAND || this == KILLAURA_CHARGE || this == DASH || this == ORB;
	}

	@Override
	public boolean attenuate() {
		return this != GOTODIM && this != PYLONTURBO && this != PYLONFLASH && this != PYLONBOOSTRITUAL && this != PYLONBOOSTSTART && this != REPEATERSURGE && this != MONUMENT && this != MONUMENTRAY && this != GAINPROGRESS && this != LORECOMPLETE;
	}

	public boolean hasWiderPitchRange() {
		return widePitch;//return this == DING || this == ORB;// || this == FLUTE;
	}

	public ChromaSound getUpshiftedPitch() {
		ChromaSound ret = (ChromaSound)this.getVariant("HI");
		return ret != null ? ret : this;
	}

	public ChromaSound getDownshiftedPitch() {
		ChromaSound ret = (ChromaSound)this.getVariant("LO");
		return ret != null ? ret : this;
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

	@Override
	public String getRelativePath() {
		return SOUND_FOLDER+relative+SOUND_EXT;
	}

	private String getFolder() {
		return folder.isEmpty() ? "" : folder+"/";
	}

	public Collection<SoundVariant> getVariants() {
		return Collections.unmodifiableCollection(variants.values());
	}

	public SoundVariant getVariant(String name) {
		return variants.get(name);
	}

	public float getRangeInterval() { // the steps need to be TWO octaves apart when using the default of 4!
		if (this == FLUTE)
			return 2;
		return 4;
	}

	static {
		/*
		FLUTE.createVariant("F1");
		FLUTE.createVariant("F2");
		FLUTE.createVariant("F3");
		FLUTE.createVariant("F4");
		FLUTE.createVariant("F5");
		FLUTE.createVariant("F6");
		FLUTE.createVariant("L1");
		FLUTE.createVariant("L2");
		FLUTE.createVariant("L3");
		 */
		for (int i = MusicKey.G4.ordinal(); i <= MusicKey.G7.ordinal(); i++) {
			String s = MusicKey.getByIndex(i).name().toLowerCase(Locale.ENGLISH);
			FLUTE.createVariant(s);
			FLUTE.createVariant(s+"_l");
			FLUTE.createVariant(s+"_s");
		}
		/*
		FLUTE.createVariant("F1_HI");
		FLUTE.createVariant("F2_HI");
		FLUTE.createVariant("F3_HI");
		FLUTE.createVariant("L1_HI");
		FLUTE.createVariant("L2_HI");
		FLUTE.createVariant("L3_HI");

		FLUTE.createVariant("F1_LO");
		FLUTE.createVariant("F2_LO");
		FLUTE.createVariant("F3_LO");
		FLUTE.createVariant("L1_LO");
		FLUTE.createVariant("L2_LO");
		FLUTE.createVariant("L3_LO");*/

		ORB.createVariant("PURE");
		ORB.createVariant("PURE_HI");
		ORB.createVariant("PURE_LO");
	}

	private static class ChromaSoundVariant extends SoundVariant<ChromaSounds> implements ChromaSound {

		protected ChromaSoundVariant(ChromaSounds s, String k) {
			super(s, k, PREFIX+SOUND_FOLDER+s.relative+"_"+k.toLowerCase(Locale.ENGLISH)+SOUND_EXT);
		}

		@Override
		public String getRelativePath() {
			return SOUND_FOLDER+root.relative+"_"+key.toLowerCase(Locale.ENGLISH)+SOUND_EXT;
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
			ReikaSoundHelper.playSound(this, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch);
		}

		public void playSound(World world, double x, double y, double z, float vol, float pitch, boolean attenuate) {
			if (world.isRemote)
				return;
			ReikaSoundHelper.playSound(this, world, x, y, z, vol/* *this.getModulatedVolume()*/, pitch, attenuate);
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
			this.playSoundNoAttenuation(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, vol, pitch, broadcast);
		}

		public void playSoundNoAttenuation(World world, double x, double y, double z, float vol, float pitch, int broadcast) {
			if (world.isRemote)
				return;
			ReikaPacketHelper.sendSoundPacket(this, world, x, y, z, vol, pitch, false, broadcast);
		}

		public void playSoundAtBlock(TileEntity te) {
			this.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}

		public void playSoundAtBlock(WorldLocation loc) {
			this.playSoundAtBlock(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
		}

		@Override
		public boolean hasWiderPitchRange() {
			return root.hasWiderPitchRange();
		}

		@Override
		public ChromaSound getUpshiftedPitch() {
			return (ChromaSound)root.getVariant(key+"_HI");
		}

		@Override
		public ChromaSound getDownshiftedPitch() {
			return (ChromaSound)root.getVariant(key+"_LO");
		}

		@Override
		public float getRangeInterval() {
			return root.getRangeInterval();
		}

	}
}
