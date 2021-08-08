#import chunk
#import math

varying vec4 vLightMapColor;

uniform sampler2D bgl_LightMapTexture;

uniform float waveRadius;
uniform float waveThickness;
uniform float waveAmplitude;
uniform float waveSpeed;

uniform float curlMovementXZ;
uniform float curlMovementY;

uniform float stretchFactor;
uniform float stretchRadius;
uniform float stretchApplication;

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
    vLightMapColor = min(texture2D(bgl_LightMapTexture, lightMapCoords)*1.5+vec4(0.25), vec4(1.0));
	
	gl_FrontColor = gl_Color;
}