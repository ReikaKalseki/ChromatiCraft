#import math
#import geometry
#import color

uniform float distance;

float scaleFactor(float dist, float f) {
    return f*min(1.0, distance/8.0)*max(0.0, min(1.0, max(0.0, 12.0/distance))*min(4.0, 0.006/dist));
}

void main() {
    vec3 coreXYD = getScreenPosWithDepth(0.5, 0.5, 0.5);
	vec2 coreXY = coreXYD.xy;
	
	float f0 = max(0.0, intensity+min(0.0, coreXYD.z)*12.0);
	
	/*
	
    vec3 baseColor = texture2D(bgl_RenderedTexture, texcoord).rgb;
	float br = getVisualBrightness(baseColor);
	
	float distv = distsq(coreXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-0.2*distv*distance)-min(1.0, 0.02/(distv*distance)));
	float distfac_vertex = max(0.0, min(1.0, 3.5-10.0*distv*distance));
	float cf = 0.0;//f0*distfac_color;
	float vf = min(0.33, br)*f0*distfac_vertex;
	
	vec2 texUV = mix(texcoord, coreXY, vf*1.0);
	
    vec4 orig = texture2D(bgl_RenderedTexture, texUV);

	vec3 invert = vec3(1.0)-orig.rgb;
	vec3 net = mix(orig.rgb, invert, cf);
	
    gl_FragColor = vec4(net.r, net.g, net.b, orig.a); */
	
	float distv = distsq(coreXY, texcoord);
	float distfac_color = max(0.0, 1.0-3.5*distv);
	float cf = f0*distfac_color;
	
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float gs = getVisualBrightness(color.rgb);
	
	float bvf = sqrt(gs)*1.5;
	
	vec2 diff = texcoord-coreXY;
	diff *= 1.0+bvf*scaleFactor(distv, f0)*0.35;
	vec2 texUV = coreXY+diff;
	
    color = texture2D(bgl_RenderedTexture, texUV);
    
    vec3 hsv = rgb2hsb(color.rgb);
	hsv.y = min(1.0, hsv.y*1.2);
	
	vec3 net = mix(color.rgb, hsb2rgb(hsv), cf);
    
    gl_FragColor = vec4(net.x, net.y, net.z, color.a);
}