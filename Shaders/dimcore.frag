void main() {
    vec2 coreXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distv = distsq(coreXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 2.0-150.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 3.0-500.0*distv));
	float cf = intensity*distfac_color;
	float vf = intensity*distfac_vertex;
	
	texcoord = mix(texcoord, coreXY, vf/8.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}