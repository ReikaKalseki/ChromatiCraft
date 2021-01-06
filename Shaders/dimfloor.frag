#import color

uniform float starFactor;

void main() {
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = 1.0-getVisualBrightness(color.rgb);
	float sat = rgb2hsb(color.rgb).y;
	
	float wiggle = intensity*pow(gs, 2.0)*0.02*pow(sat, 2.0);
	wiggle += starFactor*intensity*pow(1-gs, 2.0)*0.04;
	
	vec2 texUV = texcoord.xy;
	texUV.y += 0.41*wiggle*sin(34.5+texUV.x*85.7+float(time)/3.3);
	texUV.x += 0.43*wiggle*sin(23.3+texUV.y*81.8+float(time)/3.1);
	
	color = texture2D(bgl_RenderedTexture, texUV);
	
	gs = getVisualBrightness(color.rgb);
	
	//vec3 shift = rgb2hsb(color.rgb);
	vec3 shift = color.rgb;
	//shift.x = max(shift.x, 0.55);
	//shift.x = min(shift.x, 0.8);
	//shift.z = gs;
	//shift.y = min(1.0, shift.y*2.0+0.125);
	//shift = hsb2rgb(shift);
	shift.y = min(1.0, shift.y*1.4+0.0625);
	shift.z = min(1.0, shift.z*2.0+0.125);
	
	float blue = shift.z+shift.y*0.4;
	
	color.rgb = mix(color.rgb, shift, intensity*pow(gs, 2.0));
	
	shift = rgb2hsb(color.rgb);
	shift.y = min(1.0, shift.y*(1.0+blue*intensity));
	color.rgb = hsb2rgb(shift);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}