#import chunk
#import math

varying vec4 vLightMapColor;

uniform vec4 glow1;
uniform vec4 glow2;
uniform vec4 glow3;
uniform vec4 glow4;
uniform vec4 glow5;
uniform vec4 glow6;
uniform vec4 glow7;
uniform vec4 glow8;
uniform vec4 glow9;
uniform vec4 glow10;
uniform vec4 glow11;
uniform vec4 glow12;
uniform vec4 glow13;
uniform vec4 glow14;
uniform vec4 glow15;

vec4 addGlow(vec4 color, vec4 glow) {

}

void main() {    
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
    //gl_FragColor = gl_Color*tex*vLightMapColor;
	vec4 color = gl_Color*tex*vLightMapColor;
	vec3 real = getReal(vert);
	
	color = addGlow(color, glow1);
	color = addGlow(color, glow2);
	color = addGlow(color, glow3);
	color = addGlow(color, glow4);
	color = addGlow(color, glow5);
	color = addGlow(color, glow6);
	color = addGlow(color, glow7);
	color = addGlow(color, glow8);
	color = addGlow(color, glow9);
	color = addGlow(color, glow10);
	color = addGlow(color, glow11);
	color = addGlow(color, glow12);
	color = addGlow(color, glow13);
	color = addGlow(color, glow14);
	color = addGlow(color, glow15);
	
	gl_FragColor = color;
}