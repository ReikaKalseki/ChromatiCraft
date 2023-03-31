#import math
#import color
#import noise

void main() {
    vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
	
	vec3 fogColor1 = vec3(0.478, 0.490, 0.459);
	vec3 fogColor2 = vec3(0.831, 0.784, 0.596);
	vec3 netFog = mix(fogColor1, fogColor2, 0.5+0.5*snoise3d(vec3(texcoord.x, float(time)*0.02, texcoord.y)));
	float dx = min(texcoord.x, 1.0-texcoord.x);
	float dy = min(texcoord.y, 1.0-texcoord.y);
	netFog *= max(0.5, 1.0-0.5*sqrt(dx*dx+dy*dy));
    
    float gs = getVisualBrightness(orig.rgb)*1.75;
	
	vec3 shifted = netFog*gs;
	
	vec3 net = mix(orig.rgb, shifted, intensity);
    
    gl_FragColor = vec4(net.x, net.y, net.z, orig.a);
}