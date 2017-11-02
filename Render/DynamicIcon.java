package Reika.ChromatiCraft.Render;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.AnimationMetadataSection;


public class DynamicIcon extends TextureAtlasSprite {

	private final TextureAtlasSprite original;

	public DynamicIcon(String tag, TextureMap map, TextureAtlasSprite ico) {
		super(ico.getIconName());
		this.copyFrom(ico);
		original = ico;
		map.mapRegisteredSprites.put(tag, this);
	}

	@Override
	public void updateAnimation() {
		original.updateAnimation();
		//ReikaJavaLibrary.pConsole("rerendering "+this+" @ ");

		int[][] data = original.getFrameTextureData(0);
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < data[i].length; k++) {
				data[i][k] = 0xffffffff;
			}
		}

		TextureUtil.uploadTextureMipmap(data, width, height, originX, originY, false, false);
	}

	@Override
	public void loadSprite(BufferedImage[] img, AnimationMetadataSection ams, boolean aniso) {
		original.loadSprite(img, ams, aniso);
		try {
			Field f = TextureAtlasSprite.class.getDeclaredField("framesTextureData");
			f.setAccessible(true);
			framesTextureData = (List)f.get(original);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void generateMipmaps(int lvls) {
		original.generateMipmaps(lvls);

		try {
			Field f = TextureAtlasSprite.class.getDeclaredField("framesTextureData");
			f.setAccessible(true);
			framesTextureData = (List)f.get(original);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearFramesTextureData()  {
		original.clearFramesTextureData();
		this.clearFramesTextureData();
	}

	@Override
	public boolean hasAnimationMetadata() {
		return original.hasAnimationMetadata();
	}

	@Override
	public void setFramesTextureData(List li) {
		original.setFramesTextureData(li);
		this.setFramesTextureData(li);
	}

	@Override
	public String toString() {
		return original.toString();
	}

	@Override
	public int[][] getFrameTextureData(int frame) {
		return original.getFrameTextureData(frame);
	}

	@Override
	public int getFrameCount() {
		return original.getFrameCount();
	}

	@Override
	public void setIconWidth(int w) {
		original.setIconWidth(w);
	}

	@Override
	public void setIconHeight(int h) {
		original.setIconHeight(h);
	}

	@Override
	public int getOriginX() {
		return original.getOriginX();
	}

	@Override
	public int getOriginY() {
		return original.getOriginY();
	}

	@Override
	public int getIconWidth() {
		return original.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return original.getIconHeight();
	}

	@Override
	public float getMinU() {
		return original.getMinU();
	}

	@Override
	public float getMaxU() {
		return original.getMaxU();
	}

	@Override
	public float getInterpolatedU(double f) {
		return original.getInterpolatedU(f);
	}

	@Override
	public float getMinV() {
		return original.getMinV();
	}

	@Override
	public float getMaxV() {
		return original.getMaxV();
	}

	@Override
	public float getInterpolatedV(double f) {
		return original.getInterpolatedV(f);
	}

}
