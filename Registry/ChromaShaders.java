package Reika.ChromatiCraft.Registry;

import java.util.Locale;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;

public enum ChromaShaders {

	PYLON(),
	AURALOC();

	private final ShaderDomain domain;
	private ShaderProgram shader;

	public static final ChromaShaders[] shaders = values();

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
			shader = ShaderRegistry.createShader(ChromatiCraft.instance, this.name().toLowerCase(Locale.ENGLISH), ChromatiCraft.class, "Shaders/", domain);
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not create shader "+this+"!", e);
		}
	}

	public static void registerAll() {
		for (int i = 0; i < shaders.length; i++) {
			ChromaShaders s = shaders[i];
			s.create();
		}
	}

}
