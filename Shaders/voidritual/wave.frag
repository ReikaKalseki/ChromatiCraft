varying vec2 vLightMapCoords;
varying vec4 vLightMapColor;

uniform sampler2D bgl_LightMapTexture;

void main() {	
	vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    vec4 light = texture2D(bgl_LightMapTexture, vec2(0.5+vLightMapCoords.x, 0.5-vLightMapCoords.y));
	gl_FragColor = gl_Color*tex*vLightMapColor;
}