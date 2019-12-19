uniform float size;

void main() {
    vec2 coreXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distv = distsq(coreXY, texcoord);
	float distfac = max(0.0, min(1.0, 1.0-0.6*distv*size)-min(1.0, 0.04/(distv*size)));
	float f = intensity*distfac;
	
	vec2 texUV = mix(texcoord, coreXY, f/7.5);
	
    vec4 orig = texture2D(bgl_RenderedTexture, texUV);
	vec3 net = mix(orig.rgb, vec3(1.0), f);
    gl_FragColor = vec4(net.r, net.g, net.b, orig.a);
}