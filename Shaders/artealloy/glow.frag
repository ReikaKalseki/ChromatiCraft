void main() {
	vec2 nodeXY = getScreenPos(0, 0, 0);
	
	float distv = distsq(nodeXY, texcoord);
	float d = 1.6+0.4*sin(time*0.053);
	float distfac_color = max(0.0, min(1.0, d-15.0*distv));
	//float distfac_color = max(0.0, min(1.0, 1.0-50.0*distv));
	//float distfac_color2 = max(0.0, min(1.0, 1.0-150.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 1.5-15.0*distv));
	float cf = intensity*distfac_color*0.8;
	//float cf = intensity*distfac_color*0.75;
	//float cf2 = intensity*distfac_color2*0.5;
	float vf = intensity*distfac_vertex*0.006;
	
	vec2 texUV = texcoord.xy;
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	vec3 c2 = vec3(1, 0.6+0.3*sin(time*0.032), 0);
	color.rgb = mix(color.rgb, c2, cf);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}