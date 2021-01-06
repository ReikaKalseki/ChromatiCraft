#import math
#import geometry

uniform float size;
uniform float thickness;

void main() {
    vec2 coreXY = getScreenPos(0.0, 0.0, 0.0);
	
	float distv = distsq(coreXY, texcoord);
	float rdist = abs(distv-size);
	float fr = max(0.0, 1.0-rdist/thickness);
	float f = intensity*fr;
	vec2 offset = texcoord-coreXY;
	vec2 distorted = texcoord-offset;
	
	vec2 texUV = mix(texcoord, distorted, f);
	
    vec4 orig = texture2D(bgl_RenderedTexture, texUV);
	vec3 net = mix(orig.rgb, vec3(1.0), f/2.0);
    gl_FragColor = vec4(net.r, net.g, net.b, orig.a);
}