#import chunk
#import math
#import color

varying vec4 vLightMapColor;
varying vec3 worldCoord;

uniform vec3 glowLocations[16];
uniform vec4 glowColor[16];

vec4 addGlow(vec4 base, vec3 pos, vec3 loc, vec4 color) {
	vec3 diff = pos-loc;
	float distxz = getDistanceXZ(diff);
	float f = max(0.0, min(1.0, color.a-0.1*pow(max(0.0, distxz-2.5*color.a), 0.85)));
	base.rgb = min(vec3(1.0), base.rgb+f*color.rgb);
	base.a = max(base.a, f);
	return base;
}

vec4 getGlow(vec3 real) {	
	vec4 color = vec4(0.0);
	for (int i = 0; i < 16; i++) {
		color = addGlow(color, real, glowLocations[i], glowColor[i]);
	}	
	return color;
}

void main() {
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    //gl_FragColor = gl_Color*tex*vLightMapColor;
	gl_FragColor = gl_Color*tex*vLightMapColor;
	
	vec4 glow = getGlow(worldCoord);
	vec3 rgv = rgb2hsb(glow.rgb);
	vec3 hsv = rgb2hsb(gl_FragColor.rgb);
	hsv.x = rgv.x;
	hsv.y = hsv.y*0.5+rgv.y*0.5;
	hsv.z = getVisualBrightness(gl_FragColor.rgb);
	vec3 blend = mix(gl_FragColor.rgb, hsb2rgb(hsv), min(0.2, glow.a));
	
	//vec3 blend = mix(gl_FragColor.rgb, glow.rgb, min(0.2, glow.a));
	
	gl_FragColor.rgb = blend+glow.rgb; //mod(worldCoord-vec3(0.01,0.01,0.01), 4.0)/4.0;//
}