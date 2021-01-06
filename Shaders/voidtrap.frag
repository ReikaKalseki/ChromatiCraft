#import math
#import geometry
#import color

uniform float distance;
uniform float rotation;

void main() {
	vec2 nodeXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distv = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.8-0.875*distv*distance));
	float distfac_vertex = max(0.0, min(1.0, 1.0-1.0*distv*distance));
	float cf = distfac_color;
	float vf = intensity*distfac_vertex;
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    float gs = getVisualBrightness(color.rgb);
	float rot = 0.125*vf*3.1416*2*rotation/pow(max(0.002, distv), 0.25);///max(0.02, min(1, distv*2.4));
	vec2 texUV = rotate(texcoord, nodeXY, rot);
	
	vec4 color2 = texture2D(bgl_RenderedTexture, texUV);
	
	float fac = max(0, (abs(rotation)-0.5))*2;
	fac *= cf;
	color.rgb = mix(color2.rgb, color.rgb, fac);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}