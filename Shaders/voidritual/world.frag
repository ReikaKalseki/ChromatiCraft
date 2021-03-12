#import chunk
#import math
#import color

varying vec4 vLightMapColor;
varying vec3 worldCoord;

uniform vec3 glowLocations[16];
uniform vec4 glowColor[16];

vec3 addGlow(vec3 base, vec3 pos, vec3 loc, vec4 color) {
	vec3 diff = pos-loc;
	float distxz = getDistanceXZ(diff);
	float f = max(0.0, min(1.0, color.a-0.1*pow(max(0.0, distxz-2.5*color.a), 0.85)));
	base = min(vec3(1.0), base+f*color.rgb);
	return base;
}

vec3 getGlow(vec3 real) {	
	vec3 color = vec3(0.0);
	for (int i = 0; i < 16; i++) {
		color = addGlow(color, real, glowLocations[i], glowColor[i]);
	}	
	return color;
}

void main() {    
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    //gl_FragColor = gl_Color*tex*vLightMapColor;
	gl_FragColor = gl_Color*tex*vLightMapColor;
	
	vec3 glow = getGlow(worldCoord);
	vec3 rgv = rgb2hsb(glow);
	vec3 hsv = rgb2hsb(gl_FragColor.rgb);
	hsv.x = rgv.x;
	hsv.y = hsv.y*0.5+rgv.y*0.5;
	hsv.z = getVisualBrightness(gl_FragColor.rgb);
	vec3 blend = hsb2rgb(hsv);
	
	gl_FragColor.rgb = blend+glow; //mod(worldCoord-vec3(0.01,0.01,0.01), 4.0)/4.0;//
}