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

uniform float glows[112]; //vec3 position, vec3 color, 1f multiplier, x16 colors

vec3 addGlow(vec3 base, vec3 pos, float x, float y, float z, float r, float g, float b, float f0) {
	vec3 diff = pos-vec3(x, y, z);
	float distxz = getDistanceXZ(diff);
	float f = max(0.0, min(1.0, f0-0.1*max(0.0, distxz-2.5*f0)));
	vec3 mult = vec3(r, g, b);
	base = min(vec3(1.0), base+f*mult);
	return base;
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
	
	vec3 color = vec3(0.0);
	for (int i = 0; i < 4; i++) {
		int idx = i*7;
		color += addGlow(color, real, glows[idx], glows[idx+1], glows[idx+2], glows[idx+3], glows[idx+4], glows[idx+5], glows[idx+6]);
	}
	vLightGlowColor = color;
	
	gl_FrontColor = gl_Color;
}