package Reika.ChromatiCraft.Registry;

import Reika.ChromatiCraft.Render.ISBRH.ArtefactRenderer;
import Reika.ChromatiCraft.Render.ISBRH.BedrockCrackRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CaveIndicatorRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CliffStoneRenderer;
import Reika.ChromatiCraft.Render.ISBRH.ColorLockRenderer;
import Reika.ChromatiCraft.Render.ISBRH.ConsoleRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalEncrustingRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalFenceRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlassRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlowRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.ChromatiCraft.Render.ISBRH.CrystallineStoneRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DecoFlowerRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DecoPlantRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DimensionDecoRenderer;
import Reika.ChromatiCraft.Render.ISBRH.DyeVineRenderer;
import Reika.ChromatiCraft.Render.ISBRH.EverFluidRenderer;
import Reika.ChromatiCraft.Render.ISBRH.GlowTreeRenderer;
import Reika.ChromatiCraft.Render.ISBRH.LampRenderer;
import Reika.ChromatiCraft.Render.ISBRH.LaserEffectorRenderer;
import Reika.ChromatiCraft.Render.ISBRH.MetaAlloyRenderer;
import Reika.ChromatiCraft.Render.ISBRH.PistonTargetRenderer;
import Reika.ChromatiCraft.Render.ISBRH.PowerTreeRenderer;
import Reika.ChromatiCraft.Render.ISBRH.RayBlendFloorRenderer;
import Reika.ChromatiCraft.Render.ISBRH.RelayRenderer;
import Reika.ChromatiCraft.Render.ISBRH.RuneRenderer;
import Reika.ChromatiCraft.Render.ISBRH.SelectiveGlassRenderer;
import Reika.ChromatiCraft.Render.ISBRH.SparklingBlockRender;
import Reika.ChromatiCraft.Render.ISBRH.SpecialShieldRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TankBlockRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TieredOreRenderer;
import Reika.ChromatiCraft.Render.ISBRH.TieredPlantRenderer;
import Reika.ChromatiCraft.Render.ISBRH.VoidRiftRenderer;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Interfaces.Registry.ISBRHEnum;

public enum ChromaISBRH implements ISBRHEnum {

	crystal(CrystalRenderer.class),
	rune(RuneRenderer.class),
	crystalStone(CrystallineStoneRenderer.class),
	tank(TankBlockRenderer.class),
	tree(PowerTreeRenderer.class),
	lamp(LampRenderer.class),
	relay(RelayRenderer.class),
	glow(CrystalGlowRenderer.class),
	vrift(VoidRiftRenderer.class),
	dimgen(DimensionDecoRenderer.class),
	glowTree(GlowTreeRenderer.class),
	colorLock(ColorLockRenderer.class),
	specialShield(SpecialShieldRenderer.class),
	glass(CrystalGlassRenderer.class),
	console(ConsoleRenderer.class),
	fence(CrystalFenceRenderer.class),
	selective(SelectiveGlassRenderer.class),
	lasereffect(LaserEffectorRenderer.class),
	rayblendFloor(RayBlendFloorRenderer.class),
	piston(PistonTargetRenderer.class),

	artefact(ArtefactRenderer.class),
	metaAlloy(MetaAlloyRenderer.class),

	encrusted(CrystalEncrustingRenderer.class),

	ore(TieredOreRenderer.class),
	plant(TieredPlantRenderer.class),
	plant2(DecoPlantRenderer.class),
	flower(DecoFlowerRenderer.class),
	sparkle(SparklingBlockRender.class),
	everfluid(EverFluidRenderer.class),
	cliffstone(CliffStoneRenderer.class),
	caveIndicator(CaveIndicatorRenderer.class),
	bedrockCrack(BedrockCrackRenderer.class),
	dyeVine(DyeVineRenderer.class),

	;

	private final Class<? extends ISBRH> renderClass;

	private int renderID;
	private ISBRH renderer;

	private static final ChromaISBRH[] list = values();

	private ChromaISBRH(Class<? extends ISBRH> render) {
		renderClass = render;
	}

	@Override
	public int getRenderID() {
		return renderID;
	}

	@Override
	public ISBRH getRenderer() {
		return renderer;
	}

	@Override
	public void setRenderPass(int pass) {
		renderer.setRenderPass(pass);
	}

	@Override
	public Class<? extends ISBRH> getRenderClass() {
		return renderClass;
	}

	@Override
	public void setRenderID(int id) {
		renderID = id;
	}

	@Override
	public void setRenderer(ISBRH r) {
		renderer = r;
	}

}
