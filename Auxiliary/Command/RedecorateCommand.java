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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class RedecorateCommand extends DragonCommandBase {

	private static final MultiMap<String, ChunkCoordIntPair> generatedChunks = new MultiMap(new HashSetFactory());

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Incorrect formatting. Use [radius] [generator]");
			return;
		}
		int r = Integer.parseInt(args[0]);
		ChromaDecorator d = ChromatiCraft.getDecorator(args[1].toLowerCase(Locale.ENGLISH));
		if (d == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Unrecognized decorator.");
			return;
		}

		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		World world = ep.worldObj;
		int x = MathHelper.floor_double(ep.posX) >> 4;
		int z = MathHelper.floor_double(ep.posZ) >> 4;

		this.loadFile(world, d);

		int n = 0;
		int rc = r >> 4;
		for (int i = -rc; i <= rc; i++) {
			for (int k = -rc; k <= rc; k++) {
				if (ReikaWorldHelper.isChunkGeneratedChunkCoords((WorldServer)world, x+i, z+k)) {
					Chunk ch = world.getChunkFromChunkCoords(x+i, z+k);
					if (this.shouldDecorateChunk(ch, d)) {
						this.decorate(world, ch, d);
						n++;
					}
				}
			}
		}

		this.updateFile(world, d);

		this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Decorator run on "+n+" chunks.");
	}

	private boolean shouldDecorateChunk(Chunk ch, ChromaDecorator d) {
		HashSet<ChunkCoordIntPair> c = (HashSet<ChunkCoordIntPair>)generatedChunks.get(d.getCommandID());
		return !c.contains(ch.getChunkCoordIntPair());
	}

	private void decorate(World world, Chunk ch, ChromaDecorator d) {
		IChunkProvider prov = world.getChunkProvider();
		IChunkProvider gen = ((ChunkProviderServer)prov).currentChunkProvider;
		d.generate(world.rand, ch.xPosition, ch.zPosition, world, gen, prov);
		generatedChunks.addValue(d.getCommandID(), ch.getChunkCoordIntPair());
	}

	@Override
	public String getCommandString() {
		return "redecorate";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	private void loadFile(World world, ChromaDecorator d) {
		generatedChunks.remove(d.getCommandID());
		File f = this.getFile(world, d);
		if (f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			for (String s : li) {
				ChunkCoordIntPair p = this.parseCoordPair(s);
				generatedChunks.addValue(d.getCommandID(), p);
			}
		}
	}

	private void updateFile(World world, ChromaDecorator d) {
		File f = this.getFile(world, d);
		try {
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> li = new ArrayList();
		for (ChunkCoordIntPair p : generatedChunks.get(d.getCommandID())) {
			li.add(this.toString(p));
		}
		ReikaFileReader.writeLinesToFile(f, li, true);
	}

	private File getFile(World world, ChromaDecorator d) {
		File f = world.getSaveHandler().getWorldDirectory();
		if (f != null) {
			return new File(f, "DIM"+world.provider.dimensionId+"/redecorate_"+d.getCommandID()+".dat");
		}
		return null;
	}

	private ChunkCoordIntPair parseCoordPair(String s) {
		String[] parts = s.split(":");
		return new ChunkCoordIntPair(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	private String toString(ChunkCoordIntPair p) {
		return p.chunkXPos+":"+p.chunkZPos;
	}

}
