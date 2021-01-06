#import math
#import geometry
#import color

uniform float distance;

uniform float scale;
uniform float clip;

uniform float dx;
uniform float dy;
uniform float dz;

void main() {
	vec2 particleXY = getScreenPos(dx, dy, dz);	
	float distv = distsq(particleXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, clip-15.0*distv*distance/scale));
	float vf = intensity*distfac_vertex;
	
	vec2 texUV1 = texcoord;
	vec2 texUV2 = texcoord;
	vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	float density = 1.0+intensity*9.0*(1.0-getVisualBrightness(color.rgb));
	texUV2.x = roundToNearest(texUV1.x, density/(float(screenWidth)));
	texUV2.y = roundToNearest(texUV1.y, density/(float(screenHeight)));
	vec2 texUV = mix(texUV1, texUV2, vf);
	
    color = texture2D(bgl_RenderedTexture, texUV);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}