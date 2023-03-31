package Reika.ChromatiCraft.Registry;

import java.util.Locale;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderHook;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;

public enum ChromaShaders implements ShaderHook {

	//Block/Entity
	PYLON(),
	DIMCORE(),
	AURALOC(),
	DATANODE(),
	PORTAL(),
	PORTAL_INCOMPLETE(),
	VOIDTRAP(),
	WARPNODE(),
	WARPNODE_OPEN(),
	STRUCTCONTROL(),
	DIMGLOWCLOUD(),
	VACGUN(),

	//FX
	LENSPARTICLE(),
	PINCHPARTICLE(),
	CRYSTALLIZEPARTICLE(),

	//Situational
	INSKYRIVER(),
	DIMFLOOR(),
	GAINPROGRESS(),
	PYLONAURA(),
	UAZONE(),
	ENDRING(),

	//Ritual-related
	PYLONTURBO$OVERBRIGHT(),
	PYLONTURBO$PINCH(ShaderDomain.GLOBAL),

	VOIDRITUAL$SPHERE(),
	VOIDRITUAL$WORLD(ShaderDomain.WORLD),

	ARTEALLOY$SHOCK(),
	ARTEALLOY$GLOW(),

	MONUMENT$GENERAL(),
	MONUMENT$CHORDS(ShaderDomain.WORLD),
	;

	private final ShaderDomain domain;
	private ShaderProgram shader;

	private float intensity;

	public boolean clearOnRender = false;
	public float rampDownFactor = 1;
	public float rampDownAmount;
	public int lingerTime;

	private int renderAge;

	private static boolean registered = false;
	public static final ChromaShaders[] shaders = values();

	private String id;

	private ChromaShaders() {
		this(ShaderDomain.GLOBALNOGUI);
	}

	private ChromaShaders(ShaderDomain d) {
		domain = d;
	}

	public ShaderProgram getShader() {
		return shader;
	}

	public void create() {
		try {
			String path = "Shaders/";
			id = this.name().toLowerCase(Locale.ENGLISH);
			if (this == GAINPROGRESS && ChromaOptions.PROGSHADER.getState()) {
				id = id+"_alt";
			}
			int idx = id.indexOf('$');
			if (idx >= 0) {
				path = path+id.substring(0, idx)+"/";
				id = id.substring(idx+1);
			}
			shader = ShaderRegistry.createShader(ChromatiCraft.instance, id, ChromatiCraft.class, path, domain);
			shader.setHook(this);
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not create shader "+this+"!", e);
		}
	}

	public static void registerAll() {
		if (registered)
			return;
		for (int i = 0; i < shaders.length; i++) {
			ChromaShaders s = shaders[i];
			s.create();
		}
		registered = true;
	}

	@Override
	public void onPreRender(ShaderProgram s) {
		s.setEnabled(intensity > 0);
		s.setIntensity(intensity);
	}

	public void setIntensity(float f) {
		intensity = f;//MathHelper.clamp_float(f, 0, 1);
	}

	public void rampUpIntensity(float linear, float factor) {
		intensity = Math.min(1, intensity*factor+linear);
	}

	public void rampDownIntensity(float linear, float factor) {
		intensity = Math.max(0, intensity*factor-linear);
	}

	public float getIntensity() {
		return intensity;
	}

	@Override
	public void onPostRender(ShaderProgram s) {
		if (clearOnRender) {
			if (!s.hasOngoingFoci()) {
				intensity = 0;
				s.clearFoci();
			}
		}
		renderAge++;
		if (renderAge > lingerTime) {
			if (rampDownAmount > 0 || rampDownFactor < 1)
				this.rampDownIntensity(rampDownAmount, rampDownFactor);
		}
	}

	@Override
	public void updateEnabled(ShaderProgram s) {
		s.setEnabled(intensity > 0);
	}

	public void refresh() {
		renderAge = 0;
	}

}
