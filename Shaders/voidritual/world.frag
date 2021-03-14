varying vec4 vLightMapColor;

void main() {    
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    //gl_FragColor = gl_Color*tex*vLightMapColor;
	gl_FragColor = gl_Color*tex*vLightMapColor;
}