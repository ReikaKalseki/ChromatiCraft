uniform float distance;
uniform float scale;
uniform float washout;

void main() {
	vec2 nodeXY = getScreenPos(0, 0, 0);
	
	float distv = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 3.0-5.0*distv*distance/scale));
	float distfac_vertex = max(0.0, min(1.0, 3.0-5.0*distv*distance/scale));
	float cf = intensity*distfac_color;
	float vf = intensity*distfac_vertex;
	
	vec2 diff = texcoord-nodeXY;
	//diff.x = -diff.x;
	vec2 new = nodeXY-diff;
	texcoord = mix(texcoord, new, vf);	
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	color.rgb = mix(color.rgb, vec3(1.0, 1.0, 1.0), cf*washout);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}