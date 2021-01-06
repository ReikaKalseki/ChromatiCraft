#import color

uniform int pylonRed;
uniform int pylonGreen;
uniform int pylonBlue;

void main() {
    vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
    
    float gs = getVisualBrightness(orig.rgb);
	
	float r = float(pylonRed)/255.0;
	float g = float(pylonGreen)/255.0;
	float b = float(pylonBlue)/255.0;
	
	vec3 hsl = rgb2hsb(clamp(orig.rgb*vec3(1.0+r/2.5*intensity, 1.0+g/2.5*intensity, 1.0+b/2.5*intensity), 0, 1));
	vec3 pylon = rgb2hsb(vec3(r, g, b));
	
	vec3 shifted = hsb2rgb(vec3(pylon.x, float(float(pylon.y)+float(hsl.y))/2.0, hsl.z));
	shifted = mix(mix(vec3(gs, gs, gs), vec3(r, g, b), 0.5), shifted, min(1.0, pylon.y*2.5+0.125));
	
	vec3 net = mix(orig.rgb, shifted, intensity);
    
    gl_FragColor = vec4(net.x, net.y, net.z, orig.a);
}