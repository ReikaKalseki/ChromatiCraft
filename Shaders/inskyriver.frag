void main() {
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = 1.0-getVisualBrightness(color.rgb);
	
	float wiggle = intensity*pow(gs, 2.0)*0.04;
	
	texcoord.y += 0.41*wiggle*sin(34.5+texcoord.x*85.7+float(time)/3.3);
	texcoord.x += 0.43*wiggle*sin(23.3+texcoord.y*81.8+float(time)/3.1);
	
	color = texture2D(bgl_RenderedTexture, texcoord);
	
	gs = getVisualBrightness(color.rgb);
	
	vec3 shift = rgb2hsb(color.rgb);
	shift.x += max(0.0, min(1.0, intensity*shift.y*0.25*sin(texcoord.y*1.1+texcoord.x*0.7+float(time)/5.7)));
	shift = hsb2rgb(shift);
	
	color.rgb = mix(color.rgb, shift, 1.0);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}