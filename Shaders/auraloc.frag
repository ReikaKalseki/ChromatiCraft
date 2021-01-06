#import math
#import geometry
#import color

void main() {
	vec2 locusXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distv = distsq(locusXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 2.0-50.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 3.0-400.0*distv));
	float cf = intensity*distfac_color;
	float vf = intensity*distfac_vertex;
	
	vec2 texUV = mix(texcoord, locusXY, vf/4.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	
	vec3 hsb = rgb2hsb(color.xyz);
	hsb.y = min(1.0, hsb.y*1.5);
	color.rgb = mix(color.rgb, hsb2rgb(hsb), cf-0.01);
    
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}