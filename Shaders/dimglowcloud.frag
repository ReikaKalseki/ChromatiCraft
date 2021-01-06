#import math
#import geometry
#import color

uniform float distance;
uniform float factor;

void main() {
	vec2 cloudXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(cloudXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-0.7*distv*distance*distance));
	float distfac_vertex = max(0.0, min(1.0, 1.0-2.5*distv*distance*distance));
	float cf = intensity*distfac_color;
	float vf = intensity*distfac_vertex;
	
	vec2 texUV = mix(texcoord, cloudXY, vf*factor/8.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	
	vec3 hsb = rgb2hsb(color.xyz);
	hsb.y = min(1.0, hsb.y*2.0);
	color.rgb = mix(color.rgb, hsb2rgb(hsb), cf-0.01);
    
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}