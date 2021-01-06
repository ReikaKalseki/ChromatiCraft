#import math
#import geometry
#import color

uniform float size;
uniform float thickness;

void main() {
    vec2 coreXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(coreXY, texcoord);
	float rdist = abs(distv-size);
	float fr = max(0.0, 1.0-0.75*rdist/thickness);
	float f = intensity*fr*0.8;
	vec2 offset = texcoord-coreXY;
	vec2 distorted = texcoord-offset;
	
	vec2 texUV = mix(texcoord, distorted, f);
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	vec3 hsb = rgb2hsb(color.rgb);
	hsb.y = 0;
	hsb.z = min(1.0, hsb.z*1.5);
	color.rgb = mix(color.rgb, hsb2rgb(hsb), f);
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}