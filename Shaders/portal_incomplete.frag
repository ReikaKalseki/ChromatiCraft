#import math
#import geometry
#import color

uniform float distance;

void main() {
	vec2 focusXY = getScreenPos(0.5, 1.0, 0.5);
	
	float distv = distsq(focusXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-15.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 2.5-4.0*distv*distance));
	float cf = intensity*distfac_color*0.8;
	float vf = intensity*distfac_vertex;
	
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	vec3 hsb = rgb2hsb(color.rgb);
	
	float wiggle = 0.5*hsb.y*vf/distance;
	
	vec2 texUV = texcoord.xy;
	texUV.y += 0.41*wiggle*cos(34.5+texcoord.x*3.7*distance+float(time)/2.3);
	texUV.x += 0.35*wiggle*sin(23.3+texcoord.y*3.1*distance+float(time)/3.8);
	
    color = texture2D(bgl_RenderedTexture, texUV);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}