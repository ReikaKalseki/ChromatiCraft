#import chunk

varying vec4 vLightMapColor;
varying vec3 worldCoord;

uniform sampler2D bgl_LightMapTexture;

void main() {
    vec4 vert = gl_Vertex;
	
	vec3 real = getReal(vert);
	worldCoord = real;
	
	gl_Position = gl_ModelViewProjectionMatrix * vert;
	
    texcoord = vec2(gl_MultiTexCoord0);
    vec2 lightMapCoords = vec2(gl_MultiTexCoord1);
	//lightMapCoords *= 15.0/16.0;
	//lightMapCoords += 1.0/32.0;
	lightMapCoords /= 256.0;
    vLightMapColor = min(texture2D(bgl_LightMapTexture, lightMapCoords)*1.5+vec4(0.25), vec4(1.0));
	
	gl_FrontColor = gl_Color;
}