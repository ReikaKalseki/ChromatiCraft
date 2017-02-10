package Reika.ChromatiCraft.Magic.Lore;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class LoreManager {

	public static final LoreManager instance = new LoreManager();

	private final KeyAssemblyPuzzle puzzle = new KeyAssemblyPuzzle();

	private static final HashMap<ChunkCoordIntPair, LoreEntry> locationMap = new HashMap();

	private LoreManager() {

	}

	public void triggerLore(EntityPlayer ep, LoreEntry e) {

	}

	@SideOnly(Side.CLIENT)
	public void addLoreNote(EntityPlayer ep, int i) {
		;//ChromaOverlays.instance.addLoreNote();
	}

	public LoreEntry getEntry(int cx, int cz) {
		return locationMap.get(new ChunkCoordIntPair(cx, cz));
	}

}
