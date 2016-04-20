package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.RevolvedPattern;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;



public class WorldGenCrystalTree extends ChromaWorldGenerator {

	private static final ArrayList<CrystalTree>[] crystalTrees = new ArrayList[4];
	private static final int[] treeHeights = {5, 7, 10, 30};
	private static final WeightedRandom<Integer> sizeRand = new WeightedRandom();

	public static final BlockKey CRYSTAL_TRUNK = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal());

	public WorldGenCrystalTree(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.5F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int size = sizeRand.getRandomEntry();
		ArrayList<CrystalTree> li = crystalTrees[size];
		FilledBlockArray tree = new FilledBlockArray(world);
		CrystalTree c = li.get(rand.nextInt(li.size()));
		c.populate(tree);
		tree.offset(x, y, z);
		if (this.checkSpace(world, x, y, z, tree)) {
			int h = Math.max(2, rand.nextInt(treeHeights[size]));
			tree.offset(0, h, 0);
			for (int i = 0; i < h; i++) {
				int n = c.trunkWidth;
				world.setBlock(x, y+i, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal(), 3);
				for (int a = 0; a < n; a++) {
					for (int b = 0; b < n; b++) {
						world.setBlock(x+a, y+i, z+b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal(), 3);
					}
				}
			}
			tree.place();
			return true;
		}
		return false;
	}

	private boolean checkSpace(World world, int x, int y, int z, FilledBlockArray tree) {
		for (Coordinate c : tree.keySet()) {
			Block b = tree.getBlockAt(c.xCoord, c.yCoord, c.zCoord);
			int meta = tree.getMetaAt(c.xCoord, c.yCoord, c.zCoord);
			boolean needsAir = false;
			if (b != ChromaBlocks.DIMGEN.getBlockInstance() || meta != DimDecoTypes.CRYSTALLEAF.ordinal()) {
				needsAir = true;
			}
			Block at = world.getBlock(c.xCoord, c.yCoord, c.zCoord);
			boolean flag = needsAir ? at.isAir(world, c.xCoord, c.yCoord, c.zCoord) : at.canBeReplacedByLeaves(world, c.xCoord, c.yCoord, c.zCoord);
			if (!flag)
				return false;
		}
		return true;
	}

	static {
		for (int i = 0; i < crystalTrees.length; i++) {
			crystalTrees[i] = new ArrayList();
		}
		sizeRand.addEntry(0, 10);
		sizeRand.addEntry(1, 7);
		sizeRand.addEntry(2, 4);
		sizeRand.addEntry(3, 1);

		for (int i = 0; i < CrystalTree.list.length; i++) {
			CrystalTree.list[i].register();
		}
	}

	private static enum CrystalTree {

		VANILLA(0, 1),
		DYE(1, 1),
		CLIPPED(0, 1),
		ARCH(0, 1),
		UMBRELLA(1, 1),
		GROVE(0, 1),
		GROVE2(0, 1),
		RINGS(1, 1),
		BOWL(1, 1),
		WAVE(2, 2),
		FIR(2, 1),
		XMAS(3, 2),
		CANOPY(3, 2);

		public final int treeSize;
		public final int trunkWidth;

		private static final CrystalTree[] list = values();

		private CrystalTree(int s, int w) {
			treeSize = s;
			trunkWidth = w;
		}

		private void register() {
			crystalTrees[treeSize].add(this);
		}

		private void populate(FilledBlockArray f) {
			BlockKey t = CRYSTAL_TRUNK;
			BlockKey l = new BlockKey(ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.CRYSTALLEAF.ordinal());
			switch(this) {
				case VANILLA:
				case CLIPPED:
				case ARCH:
					for (int i = 0; i <= 2; i++) {
						int r = i == 2 ? 1 : 2;
						for (int a = -r; a <= r; a++) {
							for (int b = -r; b <= r; b++) {
								if (this == VANILLA || (this == ARCH && i == 0) || Math.abs(a) != 2 || Math.abs(b) != 2) {
									if (this != ARCH || i == 2 || (a == 0 && b == 0) || Math.abs(a) > 1 || Math.abs(b) > 1) {
										if (a != 0 || b != 0) {
											f.setBlock(a, i, b, l);
										}
										else {
											f.setBlock(a, i, b, t);
										}
									}
								}
							}
						}
					}
					f.setBlock(0, 3, 0, l);
					f.setBlock(1, 3, 0, l);
					f.setBlock(-1, 3, 0, l);
					f.setBlock(0, 3, 1, l);
					f.setBlock(0, 3, -1, l);
					break;
				case DYE: {
					int[] r = {1, 2, 1, 2, 1, 2, 1};
					for (int i = 0; i < r.length; i++) {
						for (int a = -r[i]; a <= r[i]; a++) {
							for (int b = -r[i]; b <= r[i]; b++) {
								if (a != 0 || b != 0) {
									f.setBlock(a, i, b, l);
								}
								else {
									f.setBlock(a, i, b, t);
								}
							}
						}
					}
					f.setBlock(0, r.length, 0, l);
					f.setBlock(1, r.length, 0, l);
					f.setBlock(-1, r.length, 0, l);
					f.setBlock(0, r.length, 1, l);
					f.setBlock(0, r.length, -1, l);
					break;
				}
				case UMBRELLA: {
					for (int h = 0; h <= 1; h++) {
						f.setBlock(-4, h, -1, l);
						f.setBlock(-4, h, 0, l);
						f.setBlock(-4, h, 1, l);

						f.setBlock(-3, h, -3, l);
						f.setBlock(-3, h, -2, l);
						f.setBlock(-3, h, 2, l);
						f.setBlock(-3, h, 3, l);

						if (h == 1) {
							f.setBlock(-3, h, -1, l);
							f.setBlock(-3, h, 0, l);
							f.setBlock(-3, h, 1, l);
						}

						for (int i = 2; i <= 6; i++) {
							int o = i > 2 && i < 6 ? 0 : 1;
							f.setBlock(i-4, h, -4+o, l);
							f.setBlock(i-4, h, 4-o, l);

							if (h == 1) {
								f.setBlock(i-4, h, -3+o, l);
								f.setBlock(i-4, h, 3-o, l);
							}
						}

						f.setBlock(3, h, -3, l);
						f.setBlock(3, h, -2, l);
						f.setBlock(3, h, 2, l);
						f.setBlock(3, h, 3, l);

						if (h == 1) {
							f.setBlock(3, h, -1, l);
							f.setBlock(3, h, 0, l);
							f.setBlock(3, h, 1, l);
						}

						f.setBlock(4, h, -1, l);
						f.setBlock(4, h, 0, l);
						f.setBlock(4, h, 1, l);
					}

					for (int a = -3; a <= 3; a++) {
						for (int b = -3; b <= 3; b++) {
							if (Math.abs(a)+Math.abs(b) <= 4) {
								f.setBlock(a, 2, b, l);
							}
						}
					}

					for (int a = -1; a <= 1; a++) {
						for (int b = -1; b <= 1; b++) {
							f.setBlock(a, 3, b, l);
						}
					}

					for (int h = 0; h < 3; h++) {
						f.setBlock(0, h, 0, t);
					}

					break;
				}
				case GROVE:
				case GROVE2: {
					int[] r = {1, 2, 2, 2, 2, 1};
					if (this == GROVE2)
						r = new int[]{1, 2, 2, 2, 1};
					for (int h = 0; h < r.length; h++) {
						for (int a = -2; a <= 2; a++) {
							for (int b = -2; b <= 2; b++) {
								if (Math.abs(a)+Math.abs(b) <= r[h]) {
									if (this == GROVE || h != 1 || (Math.abs(a) <= 1 || Math.abs(b) <= 1)) {
										if (a != 0 || b != 0) {
											f.setBlock(a, h, b, l);
										}
										else {
											f.setBlock(a, h, b, t);
										}
									}
								}
							}
						}
					}
					f.setBlock(0, r.length, 0, l);
					break;
				}
				case RINGS: {
					for (int h = 0; h <= 1; h++) {
						for (int a = -2; a <= 2; a++) {
							for (int b = -2; b <= 2; b++) {
								if (Math.abs(a)+Math.abs(b) <= h+1) {
									if (a != 0 || b != 0) {
										f.setBlock(a, h, b, l);
									}
									else {
										f.setBlock(a, h, b, t);
									}
								}
							}
						}
					}
					for (int h = 2; h <= 6; h++) {
						if (h%2 == 0) {
							for (int a = -3; a <= 3; a++) {
								for (int b = -3; b <= 3; b++) {
									if (Math.abs(a) < 3 || Math.abs(b) < 3) {
										if (a != 0 || b != 0) {
											f.setBlock(a, h, b, l);
										}
										else {
											f.setBlock(a, h, b, t);
										}
									}
								}
							}
							f.setBlock(-4, h, 0, l);
							f.setBlock(4, h, 0, l);
							f.setBlock(0, h, 4, l);
							f.setBlock(0, h, -4, l);
						}
						else {
							for (int a = -3; a <= 3; a++) {
								for (int b = -3; b <= 3; b++) {
									if (Math.abs(a)+Math.abs(b) <= 3) {
										if (a != 0 || b != 0) {
											f.setBlock(a, h, b, l);
										}
										else {
											f.setBlock(a, h, b, t);
										}
									}
								}
							}
						}
					}
					for (int a = -2; a <= 2; a++) {
						for (int b = -2; b <= 2; b++) {
							if (Math.abs(a) < 2 || Math.abs(b) < 2) {
								if (a != 0 || b != 0) {
									f.setBlock(a, 7, b, l);
								}
								else {
									f.setBlock(a, 7, b, t);
								}
							}
						}
					}
					for (int a = -1; a <= 1; a++) {
						for (int b = -1; b <= 1; b++) {
							f.setBlock(a, 8, b, l);
						}
					}
					break;
				}
				case BOWL: {
					BlockKey[][] slice = new BlockKey[][]{
							{null, null, l, l, l, null, null},
							{null, l, null, null, null, l, null},
							{l, null, null, null, null, null, l},
							{l, null, null, t, null, null, l},
							{l, null, null, null, null, null, l},
							{null, l, null, null, null, l, null},
							{null, null, l, l, l, null, null},
					};
					for (int h = 0; h <= 2; h++) {
						for (int a = 0; a < slice.length; a++) {
							for (int b = 0; b < slice.length; b++) {
								BlockKey bk = slice[a][b];
								if (bk != null) {
									int dx = a-3;
									int dz = b-3;
									f.setBlock(dx, h, dz, bk.blockID, bk.metadata);
								}
							}
						}
					}
					for (int a = -2; a <= 2; a++) {
						for (int b = -2; b <= 2; b++) {
							if (Math.abs(a) < 2 || Math.abs(b) < 2) {
								if (a != 0 || b != 0) {
									f.setBlock(a, 3, b, l);
								}
								else {
									f.setBlock(a, 3, b, t);
								}
							}
						}
					}
					f.setBlock(0, 4, 0, l);
					f.setBlock(1, 4, 0, l);
					f.setBlock(-1, 4, 0, l);
					f.setBlock(0, 4, 1, l);
					f.setBlock(0, 4, -1, l);

					f.setBlock(1, 2, 2, l);
					f.setBlock(1, 2, -2, l);
					f.setBlock(-1, 2, 2, l);
					f.setBlock(-1, 2, -2, l);

					f.setBlock(2, 2, 1, l);
					f.setBlock(-2, 2, 1, l);
					f.setBlock(2, 2, -1, l);
					f.setBlock(-2, 2, -1, l);
					break;
				}
				case WAVE: {
					RevolvedPattern p = new RevolvedPattern(f.world, trunkWidth, 13, 5);
					for (int i = 0; i < 13; i++)
						p.addBlock(t, i, 0, 0);

					int[][] r = {
							{4, 4, 4, 3, 1},
							{5, 5, 5, 4, 3, 1},
							{4, 4, 4, 3, 1},
							{2, 3, 2},
							{2, 2, 1},
							{4, 4, 4, 3, 1},
							{5, 5, 5, 4, 3},
							{4, 4, 4, 3, 1},
							{3, 3, 3, 1},
							{2, 2, 1},
							{2, 1},
							{1},
					};

					for (int h = 0; h < r.length; h++) {
						int[] dr = r[h];
						for (int d = 0; d < dr.length; d++) {
							int b = d == 0 ? 1 : 0;
							int m = b+dr[d];
							for (int a = b; a < m; a++) {
								p.addBlock(l, h, a, d);
							}
						}
					}
					p.calculate();
					p.populate(f);
					break;
				}
				case FIR: {
					int[] d = {3, 2, 2, 4, 5, 4, 2, 3, 2, 1};
					int[] r = {3, 2, 2, 3, 4, 3, 2, 3, 2, 1};
					for (int h = 0; h < d.length; h++) {
						int dr = r[h];
						for (int a = -dr; a <= dr; a++) {
							for (int b = -dr; b <= dr; b++) {
								if (Math.abs(a)+Math.abs(b) <= d[h]) {
									if (a != 0 || b != 0) {
										f.setBlock(a, h, b, l);
									}
									else {
										f.setBlock(a, h, b, t);
									}
								}
							}
						}
					}
					f.setBlock(0, d.length, 0, l);
					for (int i = 2; i < 6; i++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
						ForgeDirection dir2 = ReikaDirectionHelper.getLeftBy90(dir);
						for (int dd = 0; dd < 2; dd++) {
							f.setBlock(dd*dir.offsetX, 0, dd*dir.offsetZ, t);
							f.setBlock(dd*dir.offsetX, 8, dd*dir.offsetZ, t);
						}
						for (int dd = 0; dd < 3; dd++) {
							f.setBlock(dd*dir.offsetX, 4, dd*dir.offsetZ, t);
						}
						f.setBlock(3*dir.offsetX+dir2.offsetX, 4, 3*dir.offsetZ+dir2.offsetZ, t);
						f.setBlock(3*dir.offsetX-dir2.offsetX, 4, 3*dir.offsetZ-dir2.offsetZ, t);
					}
					break;
				}
				case XMAS: {
					RevolvedPattern p = new RevolvedPattern(f.world, trunkWidth, 19, 5);
					for (int i = 0; i < 19; i++)
						p.addBlock(t, i, 0, 0);

					int[][] r = {

					};

					for (int h = 0; h < r.length; h++) {
						int[] dr = r[h];
						for (int d = 0; d < dr.length; d++) {
							int b = d == 0 ? 1 : 0;
							int m = b+dr[d];
							for (int a = b; a < m; a++) {
								p.addBlock(l, h, a, d);
							}
						}
					}
					p.calculate();
					p.populate(f);
					break;
				}
				case CANOPY: {
					RevolvedPattern p = new RevolvedPattern(f.world, trunkWidth, 7, 7);
					for (int i = 0; i < 19; i++)
						p.addBlock(t, i, 0, 0);

					int[][] r = {
							{5, 5, 5, 4, 3, 1},
							{5, 6, 6, 5, 4, 3},
							{5, 5, 5, 4, 3, 1},
							{4, 5, 4, 3, 2},
							{3, 4, 3, 2},
							{2, 3, 2},
							{1, 1}
					};

					for (int h = 0; h < r.length; h++) {
						int[] dr = r[h];
						for (int d = 0; d < dr.length; d++) {
							int b = d == 0 ? 1 : 0;
							int m = b+dr[d];
							for (int a = b; a < m; a++) {
								p.addBlock(l, h, a, d);
							}
						}
					}
					p.calculate();
					p.populate(f);
					break;
				}
			}
			//f.setBlock(0, 0, 0, Blocks.wool, this.ordinal());
		}
	}

}
