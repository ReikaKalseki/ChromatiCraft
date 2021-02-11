package Reika.ChromatiCraft.Magic;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Structure.MusicTempleStructure;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.MusicScore.Note;
import Reika.DragonAPI.Instantiable.MusicScore.ScoreTrack;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CrystalMusicTemple {

	private static final ExpectedMelody melody = new ExpectedMelody();
	private static final Collection<Coordinate> pitLocations = new ArrayList();

	static {
		try(InputStream in = ChromatiCraft.class.getResourceAsStream("Resources/templesong.dat")) {
			melody.load(in);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		pitLocations.add(new Coordinate(2, -3, 0));
		pitLocations.add(new Coordinate(-2, -3, 0));
		pitLocations.add(new Coordinate(0, -3, 2));
		pitLocations.add(new Coordinate(0, -3, -2));
	}

	private final LinkedList<MusicKey>[] tracks = new LinkedList[16];
	private final MusicTempleStructure structure = new MusicTempleStructure();
	private final float[] renderActivity = new float[8];

	private final ArrayList<ActiveNote> playing = new ArrayList();
	private final ArrayList<Particle> particles = new ArrayList();
	private SongSections lastSection;
	private int playTick = -1;

	private Coordinate tileLocation;
	private boolean isStructureComplete;
	private boolean isCorrectMelody;
	private float renderBrightness;

	public CrystalMusicTemple() {

	}

	public void setCore(TileEntityCrystalMusic te) {
		tileLocation = new Coordinate(te);
		structure.setOrigin(te.worldObj, tileLocation);
	}

	public void onMusicStart(World world, ScoreTrack track0) {
		if (track0 == null || track0.isEmpty())
			return;
		/*
		int len = track0.getLengthInTicks()/8+1; //8 ticks/beat
		ExpectedNote[] data = new ExpectedNote[len];
		for (int i = 0; i < data.length; i++) {
			if (i == data.length-1) {
				data[i] = new ExpectedNote();
			}
			else {
				int tick = i*8;
				NoteData notes = track0.getNoteAt(tick);
				data[i] = new ExpectedNote(notes.keys());
			}
		}*//*
		int len = track0.getLengthInTicks();
		ExpectedNote[] data = new ExpectedNote[len/4+1]; //8 ticks/beat + 1 beat after to ensure end; resolutioin is 8th notes
		/*
		HashSet<MusicKey> active = null;
		int last = -1;
		for (int t = 0; t < len; t++) {
			int idx = t/8;
			if (idx != last) {
				if (active != null) {
					data[last] = new ExpectedNote(active);
					ReikaJavaLibrary.pConsole("Putting "+data[last]+" @ "+last);
				}
				active = new HashSet();
			}
			last = idx;
			NoteData nd = track0.getNoteAt(t);
			if (nd != null) {
				active.addAll(nd.keys());
			}
		}
		 *//*
		for (int i = 0; i < data.length; i++) {
			data[i] = new ExpectedNote(i*4);
		}
		for (NoteData nd : track0.getNotes()) {
			int dl = ReikaMathLibrary.roundToNearestX(4, nd.length());
			int time = ReikaMathLibrary.roundToNearestX(2, nd.tick);
			time = ReikaMathLibrary.roundDownToX(4, time);
			for (int t = time; t < time+dl; t++) {
				int idx = t/4;
				//ReikaJavaLibrary.pConsole("Adding "+nd.keys()+" to "+idx+" = "+data[idx]+" from "+time+"+"+dl+"="+(dl/4)+"x8ths");
				data[idx].add(nd.keys());
			}
		}
		  */
		if (isStructureComplete) {
			try {
				this.setMelody(track0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (isCorrectMelody) {
				NBTTagCompound tag = new NBTTagCompound();
				this.writeStartData(tag);
				ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.MUSICTEMPLESTART.ordinal(), tag, new PacketTarget.RadiusTarget(world, tileLocation, 256));
				//spillFluid(world);
			}
		}
	}

	private void spillFluid(World world) {
		for (Coordinate c : pitLocations) {
			c.offset(tileLocation).setBlock(world, Blocks.stonebrick);
		}
	}

	private void setMelody(ScoreTrack track) {
		track = track.alignToGrid(4);
		isCorrectMelody = true;
		MusicKey lowest = track.getLowest();
		if (lowest.getNote() != melody.lowest.rootNote) { //not even the right key
			isCorrectMelody = false;
			return;
		}
		int diff = lowest.octaveNumber-melody.lowest.octaveOffset;
		for (ExpectedNote e : melody.melody) {
			if (e != null) {
				e.setOctaveOffset(diff);
				if (!e.match(track)) {
					isCorrectMelody = false;
					return;
				}
			}
		}
	}

	public void onNote(World world, MusicKey m, int track, int tick) {
		if (track == 0 && isStructureComplete && isCorrectMelody) {
			if (world.isRemote) {
				this.onNotePlayed(m, tick);
			}
			else {
				if (SongSections.getSectionAt(tick) == SongSections.TWO) {
					this.spillFluid(world);
				}
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.MUSICTEMPLE.ordinal(), world, tileLocation, 256, m.ordinal(), track, tick);
			}
		}
	}

	public void onMusicEnd(World world) {
		if (isStructureComplete && isCorrectMelody) {
			this.onSongComplete(world);
		}
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.MUSICTEMPLEEND.ordinal(), world, tileLocation, 256);
		this.checkStructure(world);
	}

	private void onSongComplete(World world) {
		tileLocation.offset(0, -2, 0).setBlock(world, Blocks.air);

		for (Coordinate c : pitLocations) {
			c.offset(tileLocation).setBlock(world, Blocks.air);
		}
	}

	public void checkStructure(World world) {
		isStructureComplete = tileLocation != null && structure.validate();
	}

	public boolean isComplete() {
		return isStructureComplete;
	}

	public boolean isPlayingMelody() {
		return isCorrectMelody;
	}

	@SideOnly(Side.CLIENT)
	public boolean isRendering() {
		return renderBrightness > 0;
	}

	@SideOnly(Side.CLIENT)
	private void onNotePlayed(MusicKey note, int tick) {
		playing.add(new ActiveNote(note));
		renderBrightness = 1.5F;
		playTick = tick;
	}

	@SideOnly(Side.CLIENT)
	private void onStartSection(SongSections s) {
		ReikaJavaLibrary.pConsole("Started "+s);
		switch(s) {
			case INTRO:
				particles.add(new Particle(ParticleTypes.RED, 0));
				particles.add(new Particle(ParticleTypes.YELLOW, 0));
				particles.add(new Particle(ParticleTypes.BLUE, 0));
				double r = 5;
				for (Particle p : particles) {
					double a = p.type.ordinal()*120;
					p.setRadialPosition(tileLocation.xCoord+0.5, tileLocation.zCoord+0.5, tileLocation.yCoord-1.75, a, r);
				}
				break;
			case ONE:
				for (Particle p : particles) {
					p.motionY = 0.075;
					p.motionX = ReikaRandomHelper.getRandomPlusMinus(0, 0.15);
					p.motionZ = ReikaRandomHelper.getRandomPlusMinus(0, 0.15);
				}
				break;
			case TWO:
				for (int i = 0; i < 6; i++)
					particles.add(new Particle(ParticleTypes.PURPLE, i));
				break;
			case THREE:
				Iterator<Particle> it = particles.iterator();
				while (it.hasNext()) {
					Particle p = it.next();
					if (p.type == ParticleTypes.PURPLE)
						it.remove();
				}
				for (int i = 0; i < 3; i++)
					particles.add(new Particle(ParticleTypes.FINAL, i));
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticles(SongSections s, float ptick) {
		double t = Minecraft.getMinecraft().theWorld.getTotalWorldTime()+ptick;//System.currentTimeMillis()/40D;
		double ft = (playTick-s.startTick)/(float)(s.endTime()-s.startTick);
		switch(s) {
			case INTRO:
				break;
			case TWO:
				double dt = 2+6*ft;
				double r = 7.5-5*ft;
				for (Particle p : particles) {
					if (p.type == ParticleTypes.PURPLE) {
						double a = (t*dt+p.listIndex*120)%360D;
						p.setRadialPosition(tileLocation.xCoord+0.5, tileLocation.zCoord+0.5, tileLocation.yCoord-1.75, a, r);
						p.lscale = 1.5F;
						p.gscale = 1.5F;
						p.size = 2F;
						p.colorDelay = 6;
					}
				}
				//lack of break intentional
			case ONE:
				for (Particle p : particles) {
					if (p.type == ParticleTypes.PURPLE)
						continue;
					//p.setRadialPosition(tileLocation.xCoord+0.5, tileLocation.zCoord+0.5, tileLocation.yCoord-1.75, p.type.ordinal()*120, 5);
					//p.motionY = 0.075;
					//p.motionX = ReikaRandomHelper.getRandomPlusMinus(0, 0.15);
					//p.motionZ = ReikaRandomHelper.getRandomPlusMinus(0, 0.15);
					for (Particle p2 : particles) {
						if (p.type == ParticleTypes.PURPLE)
							continue;
						double f2 = 0.15*0.2;
						double x2 = p2.posX;
						double y2 = p2.posY;
						double z2 = p2.posZ;
						if (p == p2) {
							x2 = tileLocation.xCoord+0.5;
							y2 = tileLocation.yCoord+0.5+3.5;
							z2 = tileLocation.zCoord+0.5;
						}
						double dx = x2-p.posX;
						double dy = y2-p.posY;
						double dz = z2-p.posZ;
						double dd = ReikaMathLibrary.py3d(dx, dy, dz);
						if (p == p2) {
							if (dd < 5)
								f2 = 0;
							else
								f2 *= 10F;
						}
						if (f2 > 0) {
							double f = Math.min(0.2, f2/(dd*dd));
							double vx = dx/dd*f;
							double vy = dy/dd*f;
							double vz = dz/dd*f;
							p.motionX += vx+ReikaRandomHelper.getRandomPlusMinus(0, 0.05);
							p.motionY += vy;
							p.motionZ += vz+ReikaRandomHelper.getRandomPlusMinus(0, 0.05);
						}
					}
				}
				break;
			case THREE:
				double dft = ft >= 0.75 ? 0 : (0.75-ft)*1.333;
				for (Particle p : particles) {
					double a = p.type == ParticleTypes.FINAL ? 120*p.listIndex : p.type.ordinal()*120+60;
					double r2 = 3.5;
					p.size = p.type == ParticleTypes.FINAL ? 1.5F : 1F;
					double h = 0.5*Math.sin(t*0.2+a*2);
					if (p.type == ParticleTypes.FINAL) {
						r2 += 4.5*dft;
						p.colorDelay = 2;
					}
					else {
						r2 += (2+3.5*Math.cos(t*0.3+a*3.5))*dft;
						h += (3*Math.sin(t*0.4+a*2.5))*dft;
					}
					p.setRadialPosition(tileLocation.xCoord+0.5, tileLocation.zCoord+0.5, tileLocation.yCoord+2.5+h, (a+t*3)%360D, r2);
				}
				break;
		}
		for (Particle p : particles) {
			p.move(tileLocation);
			p.spawnFX();
		}
	}

	private static class Particle implements ColorController {

		private final ParticleTypes type;
		private final int listIndex;

		private double posX;
		private double posY;
		private double posZ;

		private double motionX;
		private double motionY;
		private double motionZ;

		private float gscale = 1;
		private float size = 1;
		private float lscale = 1;
		private int colorDelay = 0;

		private double angle;
		private double angleSpeed;

		private static final int fxLife = 25;//12;

		private Particle(ParticleTypes p, int idx) {
			type = p;
			listIndex = idx;
		}

		public double getDistance(Particle p) {
			return ReikaMathLibrary.py3d(p.posX-posX, p.posY-posY, p.posZ-posZ);
		}

		private void move(Coordinate ctr) {
			posX += motionX;
			posY += motionY;
			posZ += motionZ;

			motionX *= 0.93;
			motionY *= 0.93;
			motionZ *= 0.93;

			angle += angleSpeed;

			posX = MathHelper.clamp_double(posX, ctr.xCoord-12, ctr.xCoord+13);
			posZ = MathHelper.clamp_double(posZ, ctr.zCoord-12, ctr.zCoord+13);
			posY = MathHelper.clamp_double(posY, ctr.yCoord-2, ctr.yCoord+6);
		}

		@SideOnly(Side.CLIENT)
		private void spawnFX() {
			EntityCCBlurFX fx = new EntityCCBlurFX(Minecraft.getMinecraft().theWorld, posX, posY, posZ);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.075, 0.2);
			fx.setIcon(type.getIcon()).setLife((int)(fxLife*lscale)).setGravity(g*gscale).setScale(2.5F*size);
			fx.setAlphaFading().setRapidExpand().setColorController(this);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		private void setRadialPosition(double x0, double z0, double y, double ang, double r) {
			angle = ang;
			posX = x0+r*Math.cos(Math.toRadians(angle));
			posY = y;
			posZ = z0+r*Math.sin(Math.toRadians(angle));
		}

		private void setPosition(double x, double y, double z) {
			posX = x;
			posY = y;
			posZ = z;
		}

		@Override
		public void update(Entity e) {

		}

		@Override
		public int getColor(Entity e) {
			int age = Math.max(0, e.ticksExisted-colorDelay);
			int mid = fxLife/4;
			float f = age <= mid ? 0 : (age-mid)*1.333F/fxLife;//e.ticksExisted/(float)fxLife;
			return ReikaColorAPI.mixColors(type.secondaryColor, type.primaryColor, f);
		}

	}

	private static enum SongSections {
		INTRO(0),
		ONE(64),
		TWO(128),
		THREE(256);

		public final int startTick;

		private static final SongSections[] list = values();
		private static final TreeMap<Integer, SongSections> timeMap = new TreeMap();

		private SongSections(int idx) {
			startTick = idx*4; //4 ticks per note index
		}

		public int endTime() {
			return this.ordinal() == list.length-1 ? melody.tickLength() : list[this.ordinal()+1].startTick;
		}

		public int length() {
			return this.endTime()-startTick;
		}

		private static SongSections getSectionAt(int t) {
			return timeMap.floorEntry(t).getValue();
		}

		static {
			for (SongSections s : list) {
				timeMap.put(s.startTick, s);
			}
		}
	}

	private enum ParticleTypes {
		BLUE(0x0CB1ED, 0x82D0EC),
		YELLOW(0x0FFE300, 0xE0C400),
		RED(0xEB2B0B, 0xF2D9E9),
		PURPLE(0xB71EFF, 0xDC96FF),
		FINAL(0x2125A0, 0xEAEEF9),
		;

		public final int primaryColor;
		public final int secondaryColor;

		private static final ParticleTypes[] list = values();

		private ParticleTypes(int c1, int c2) {
			primaryColor = c1;
			secondaryColor = c2;
		}

		public ChromaIcons getIcon() {
			switch(this) {
				case BLUE:
					return ChromaIcons.FADE_SNOW;
				case RED:
					return ChromaIcons.FADE_CLOUD;
				case YELLOW:
					return ChromaIcons.FADE_RAY;
				case PURPLE:
					return ChromaIcons.FADE_GENTLE;
				default:
					return ChromaIcons.CENTER;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void render(float ptick) {
		if (playTick == -1)
			renderBrightness = Math.max(0, renderBrightness-0.0125F);
		if (renderBrightness <= 0 || playTick < 0)
			return;
		SongSections sec = SongSections.getSectionAt(playTick);
		if (sec != lastSection)
			this.onStartSection(sec);
		if (!Minecraft.getMinecraft().isGamePaused())
			this.updateParticles(sec, ptick);
		lastSection = sec;
		ReikaTextureHelper.bindTerrainTexture();
		for (int i = 0; i < 8; i++) {
			renderActivity[i] = 0;
		}
		Iterator<ActiveNote> it = playing.iterator();
		while (it.hasNext()) {
			ActiveNote a = it.next();
			a.age++;
			float f = a.intensity();
			if (f > 0) {
				int p = a.getPillar();
				renderActivity[p] = Math.max(renderActivity[p], f);
			}
			else {
				it.remove();
			}
		}

		World world = Minecraft.getMinecraft().theWorld;

		for (int i = 0; i < 8; i++) {
			float f = renderActivity[i];
			Tessellator v5 = Tessellator.instance;
			IIcon ico = ChromaIcons.CAUSTICS_CRYSTAL.getIcon();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			int c = ReikaColorAPI.mixColors(0x1ED88B, 0x0096FF, f);
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, Math.min(1, renderBrightness)));
			double o = 0.001;

			int sx = 4;
			int sy = 4;

			Map<Coordinate, BlockKey> map = structure.getPillar(i);
			for (Entry<Coordinate, BlockKey> e : map.entrySet()) {
				//Coordinate c2 = e.getKey().offset(tileLocation);
				Coordinate c2 = e.getKey();
				BlockKey bk = e.getValue();
				v5.addTranslation(c2.xCoord, c2.yCoord, c2.zCoord);

				int x = c2.xCoord+tileLocation.xCoord;
				int y = c2.yCoord+tileLocation.yCoord;
				int z = c2.zCoord+tileLocation.zCoord;

				double ux = ((x%sx)/(float)sx)*16;
				double uy = (((y+z)%sy)/(float)sy)*16;

				float u = ico.getInterpolatedU(ux);
				float v = ico.getInterpolatedV(uy);
				float du = ico.getInterpolatedU(ux+16D/sx);
				float dv = ico.getInterpolatedV(uy+16D/sy);

				if (bk.blockID.shouldSideBeRendered(world, x, y, z-1, ForgeDirection.NORTH.ordinal())) {
					v5.addVertexWithUV(0-o, 0-o, 0-o, u, v);
					v5.addVertexWithUV(1+o, 0-o, 0-o, du, v);
					v5.addVertexWithUV(1+o, 1+o, 0-o, du, dv);
					v5.addVertexWithUV(0-o, 1+o, 0-o, u, dv);
				}

				if (bk.blockID.shouldSideBeRendered(world, x, y, z+1, ForgeDirection.SOUTH.ordinal())) {
					v5.addVertexWithUV(0-o, 1+o, 1+o, u, dv);
					v5.addVertexWithUV(1+o, 1+o, 1+o, du, dv);
					v5.addVertexWithUV(1+o, 0-o, 1+o, du, v);
					v5.addVertexWithUV(0-o, 0-o, 1+o, u, v);
				}

				if (bk.blockID.shouldSideBeRendered(world, x-1, y, z, ForgeDirection.WEST.ordinal())) {
					v5.addVertexWithUV(0-o, 1+o, 0-o, u, dv);
					v5.addVertexWithUV(0-o, 1+o, 1+o, du, dv);
					v5.addVertexWithUV(0-o, 0-o, 1+o, du, v);
					v5.addVertexWithUV(0-o, 0-o, 0-o, u, v);
				}

				if (bk.blockID.shouldSideBeRendered(world, x+1, y, z, ForgeDirection.EAST.ordinal())) {
					v5.addVertexWithUV(1+o, 0-o, 0-o, u, v);
					v5.addVertexWithUV(1+o, 0-o, 1+o, du, v);
					v5.addVertexWithUV(1+o, 1+o, 1+o, du, dv);
					v5.addVertexWithUV(1+o, 1+o, 0-o, u, dv);
				}

				if (bk.blockID.shouldSideBeRendered(world, x, y+1, z, ForgeDirection.UP.ordinal())) {
					v5.addVertexWithUV(0-o, 1+o, 0-o, u, v);
					v5.addVertexWithUV(1+o, 1+o, 0-o, du, v);
					v5.addVertexWithUV(1+o, 1+o, 1+o, du, dv);
					v5.addVertexWithUV(0-o, 1+o, 1+o, u, dv);
				}

				if (bk.blockID.shouldSideBeRendered(world, x, y-1, z, ForgeDirection.DOWN.ordinal())) {
					v5.addVertexWithUV(0-o, 0-o, 1+o, u, dv);
					v5.addVertexWithUV(1+o, 0-o, 1+o, du, dv);
					v5.addVertexWithUV(1+o, 0-o, 0-o, du, v);
					v5.addVertexWithUV(0-o, 0-o, 0-o, u, v);
				}

				v5.addTranslation(-c2.xCoord, -c2.yCoord, -c2.zCoord);
			}
			v5.draw();
		}
	}

	private void writeStartData(NBTTagCompound tag) {
		tag.setBoolean("complete", isStructureComplete);
		tag.setBoolean("song", isCorrectMelody);
		if (tileLocation != null)
			tileLocation.writeToNBT("tile", tag);
	}

	@SideOnly(Side.CLIENT)
	public void onStart(NBTTagCompound tag) {
		particles.clear();
		playing.clear();
		lastSection = null;
		playTick = 0;
		isCorrectMelody = tag.getBoolean("song");
		isStructureComplete = tag.getBoolean("complete");
		tileLocation = Coordinate.readFromNBT("tile", tag);
	}

	@SideOnly(Side.CLIENT)
	public void onEnd() {
		particles.clear();
		playTick = -1;
		lastSection = null;
	}

	private static class ActiveNote {

		private final MusicKey note;

		private int age;

		private ActiveNote(MusicKey m) {
			note = m;
		}

		private int getPillar() {
			ReikaMusicHelper.Note n = note.getNote();
			int idx = n.keyIndex+1;
			if (n == ReikaMusicHelper.Note.FSHARP)
				idx = ReikaMusicHelper.Note.F.keyIndex+1;
			if (n == ReikaMusicHelper.Note.B && note.ordinal() >= MusicKey.B7.ordinal())
				idx = 0;
			return idx;
		}

		private float intensity() {
			return age < 40 ? 1 : 1-(age-40)/60F;
		}

	}

	private static class ExpectedMelody {

		private final LinkedList<ExpectedNote> melody = new LinkedList();

		private RelativeKey lowest;

		private void load(InputStream in) throws Exception {
			melody.clear();
			lowest = null;

			int tick = 0;
			ArrayList<String> li = ReikaFileReader.getFileAsLines(in, false, Charset.defaultCharset());
			for (String s : li) {
				if (s.startsWith("--"))
					continue;
				ExpectedNote e = null;
				if (s.equalsIgnoreCase("null")) {

				}
				else {
					int idxc = s.indexOf(':');
					int idxl = s.indexOf('[');
					int idxr = s.indexOf(']');
					String flag = s.substring(0, idxc);
					String[] keys = s.substring(idxl+1, idxr).split(",");
					e = new ExpectedNote(tick);
					for (String s2 : keys) {
						RelativeKey rk = RelativeKey.parse(s2);
						e.permitted.add(rk);
						if (lowest == null || lowest.isAbove(rk))
							lowest = rk;
					}
					e.allowEmpty = Boolean.parseBoolean(flag);
				}
				melody.add(e);
				tick += 4;
			}

			//ReikaJavaLibrary.pConsole(melody);
		}

		public int tickLength() {
			return melody.getLast().tick+8;
		}

	}

	private static class ExpectedNote {

		public final int tick;
		private final HashSet<RelativeKey> permitted = new HashSet();

		private boolean allowEmpty = false;

		private ExpectedNote(int t, Collection<RelativeKey> c) {
			this(t, c != null ? c.toArray(new RelativeKey[c.size()]) : null);
		}

		private void setOctaveOffset(int off) {
			for (RelativeKey rk : permitted) {
				rk.scanOffset = off;
			}
		}

		private boolean match(ScoreTrack s) {
			for (int d = -12; d <= 12; d += 4) { //add a little timing flexibility
				Collection<Note> c = s.getActiveNotesAt(tick+d);
				if (this.match(c))
					return true;
			}
			return false;
		}

		private boolean match(Collection<Note> c) {
			if (this.requireEmpty())
				return c.isEmpty();
			if (c.isEmpty())
				return allowEmpty;
			for (Note n : c) {
				boolean flag = false;
				for (RelativeKey rk : permitted) {
					if (rk.match(n)) {
						flag = true;
						break;
					}
				}
				if (!flag)
					return false;
			}
			return true;
		}

		private ExpectedNote(int t, RelativeKey... keys) {
			tick = t;

			if (keys == null || keys.length == 0) {
				allowEmpty = true;
			}
			else {
				boolean flag = false;
				for (RelativeKey mk : keys) {
					if (mk == null)
						allowEmpty = true;
					else
						permitted.add(mk);
				}
			}
		}

		private void add(Collection<RelativeKey> keys) {
			allowEmpty = keys.isEmpty();
			for (RelativeKey key : keys) {
				this.add(key);
			}
		}

		private void add(RelativeKey key) {
			allowEmpty |= key == null;
			if (key != null)
				permitted.add(key);
		}

		private boolean requireEmpty() {
			return permitted.isEmpty();
		}

		@Override
		public String toString() {
			return tick+": "+allowEmpty+" & "+permitted;
		}

	}

	private static class RelativeKey {

		private final int octaveOffset;
		private final ReikaMusicHelper.Note rootNote;

		private int scanOffset = 0;

		private RelativeKey(ReikaMusicHelper.Note n, int o) {
			rootNote = n;
			octaveOffset = o;
		}

		public boolean match(Note n) {
			return n.key.getNote() == rootNote && n.key.octaveNumber == octaveOffset+scanOffset;
		}

		public boolean isAbove(RelativeKey rk) {
			return rk.octaveOffset < octaveOffset || (rk.octaveOffset == octaveOffset && rk.rootNote.ordinal() < rootNote.ordinal());
		}

		public static RelativeKey parse(String s) {
			int offset = Character.getNumericValue(s.charAt(s.length()-1));
			ReikaMusicHelper.Note n = ReikaMusicHelper.Note.getNoteByName(s.substring(0, s.length()-1));
			return new RelativeKey(n, offset);
		}

		@Override
		public int hashCode() {
			return rootNote.ordinal() | (octaveOffset << 4) | (scanOffset << 8);
		}

		@Override
		public String toString() {
			return rootNote+"@"+octaveOffset+"+"+scanOffset;
		}

	}

}
