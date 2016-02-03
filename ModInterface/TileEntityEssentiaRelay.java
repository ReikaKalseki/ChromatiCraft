package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.ModInterface.EssentiaNetwork.EssentiaMovement;
import Reika.ChromatiCraft.ModInterface.EssentiaNetwork.EssentiaPath;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;


@Strippable(value={"thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityEssentiaRelay extends TileEntityChromaticBase implements IEssentiaTransport, BreakAction {

	//private static final int PATH_DURATION = 30;
	public static final int SEARCH_RANGE = 8;

	private final StepTimer scanTimer = new StepTimer(50);

	EssentiaNetwork network;

	private final Collection<EssentiaPath> activePaths = new ArrayList();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ESSENTIARELAY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		for (EssentiaPath p : activePaths) {
			p.update(world, x, y, z);
		}
		activePaths.clear();

		scanTimer.update();
		if (scanTimer.checkCap()) {
			this.scan(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.scan(world, x, y, z);
	}

	void scan(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		network = new EssentiaNetwork();
		//network.addTile(this);
		for (int i = -SEARCH_RANGE; i <= SEARCH_RANGE; i++) {
			for (int j = -SEARCH_RANGE; j <= SEARCH_RANGE; j++) {
				for (int k = -SEARCH_RANGE; k <= SEARCH_RANGE; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					TileEntity te = world.getTileEntity(dx, dy, dz);
					if (te instanceof IEssentiaTransport) {
						if (te != this) {
							if (te instanceof TileEntityEssentiaRelay) {
								TileEntityEssentiaRelay tr = (TileEntityEssentiaRelay)te;
								if (tr.network != null)
									network.merge(tr.network);
							}
							network.addTile(this, te);
						}
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(network, !world.isRemote);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private void addPath(EssentiaPath p) {
		activePaths.add(p);
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return true;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {

	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		return null;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return 36;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		EssentiaMovement r = network != null ? network.removeEssentia(this, face, aspect, amount) : null;
		if (r != null) {
			for (EssentiaPath p : r.paths()) {
				this.addPath(p);
			}
			return r.totalAmount;
		}
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		EssentiaMovement s = network != null ? network.addEssentia(this, face, aspect, amount) : null;
		if (s != null) {
			for (EssentiaPath p : s.paths()) {
				this.addPath(p);
			}
			return s.totalAmount;
		}
		return 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		TileEntity te = this.getAdjacentTileEntity(face);
		return te instanceof IEssentiaTransport	? ((IEssentiaTransport)te).getSuctionType(face.getOpposite()) : null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		Aspect a = this.getEssentiaType(face);
		return a != null && network != null ? network.countEssentia(a) : 0;
	}

	@Override
	public int getMinimumSuction() {
		return 24;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	public void breakBlock() {
		network.reset();
	}

}
