varying vec4 vLightMapColor;
varying vec3 vLightGlowColor;

void main() {    
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    //gl_FragColor = gl_Color*tex*vLightMapColor;
	gl_FragColor = gl_Color*tex*vLightMapColor;
	gl_FragColor.rgb = vLightGlowColor;
}