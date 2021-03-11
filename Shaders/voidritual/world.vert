#import chunk
#import math

varying vec4 vLightMapColor;
varying vec3 vLightGlowColor;

uniform sampler2D bgl_LightMapTexture;

uniform float waveRadius;
uniform float waveThickness;
uniform float waveAmplitude;

uniform float curlMovementXZ;
uniform float curlMovementY;

uniform float stretchFactor;
uniform float stretchRadius;
uniform float stretchApplication;

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

vec3 addGlow(vec3 color, vec3 pos, vec4 glow) {
	vec3 diff = pos-glow.xyz;
	float distxz = getDistanceXZ(diff);
	float f = max(0.0, glow.w-0.1*distxz);
	color.r += f*glow.r*0+f;	
	color.g += f*glow.g*0;
	color.b += f*glow.b*0;
	return color;
}

void main() {
    vec4 vert = gl_Vertex;
	
	//vert.y += 2.5*sin(time*0.02+getX(vert)*0.175);
	
	vec3 real = getReal(vert);
	vec3 diff = real-focus;
	float distxz = getDistanceXZ(diff);
	float rdist = abs(distxz-waveRadius);
	float fr = max(0.0, 1.0-rdist/waveThickness);
	float f = intensity*fr;
	real.y += waveAmplitude*f;
	real.y += curlMovementY*distxz*intensity;
	real.x = mix(real.x, focus.x, curlMovementXZ*intensity);
	real.z = mix(real.z, focus.z, curlMovementXZ*intensity);
	
	diff = real-focus;
	diff *= mix(1.0, stretchFactor, intensity*max(0.0, 1.0-distxz/stretchRadius));
	vec3 repl = focus+diff;
	real.x = mix(real.x, repl.x, stretchApplication);
	real.z = mix(real.z, repl.z, stretchApplication);
	real.y -= (stretchFactor-1.0)*stretchApplication;
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(getRelativeCoord(real.xyz), vert.w);
	
    texcoord = vec2(gl_MultiTexCoord0);
    vec2 lightMapCoords = vec2(gl_MultiTexCoord1);
	//lightMapCoords *= 15.0/16.0;
	//lightMapCoords += 1.0/32.0;
	lightMapCoords /= 256.0;
    vLightMapColor = min(texture2D(bgl_LightMapTexture, lightMapCoords)*2.0, vec4(1.0));
	
	vec3 color = vec3(0.0, 0.0, 0.0);
	color = addGlow(color, real, glow1);
	color = addGlow(color, real, glow2);
	color = addGlow(color, real, glow3);
	color = addGlow(color, real, glow4);
	color = addGlow(color, real, glow5);
	color = addGlow(color, real, glow6);
	color = addGlow(color, real, glow7);
	color = addGlow(color, real, glow8);
	color = addGlow(color, real, glow9);
	color = addGlow(color, real, glow10);
	color = addGlow(color, real, glow11);
	color = addGlow(color, real, glow12);
	color = addGlow(color, real, glow13);
	color = addGlow(color, real, glow14);
	color = addGlow(color, real, glow15);
	vLightGlowColor = color;
	
	gl_FrontColor = gl_Color;
}