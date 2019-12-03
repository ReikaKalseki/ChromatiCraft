uniform float distance;

void main() {	
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = getVisualBrightness(color.rgb);
	
	float wiggle = (0.125+gs*0.875)*0.05;
	
	texcoord.y += 0.41*wiggle*sin(34.5+texcoord.x*85.7+float(time)/2.3);
	texcoord.x += 0.33*wiggle*sin(23.3+texcoord.y*81.8+float(time)/2.1);
	
    color = texture2D(bgl_RenderedTexture, texcoord);
	
	vec3 color2 = color.rgb;
	color2.r += 0.5+0.5*sin(45.4+float(time)/4.7);
	color2.g += 0.5+0.5*sin(27.9+float(time)/5.3);
	color2.b += 0.5+0.5*sin(36.1+float(time)/5.1);
	
	color.rgb = mix(color.rgb, color2, cf);
	
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}