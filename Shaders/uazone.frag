#import chunk
#import math
#import color
#import noise

varying vec4 vLightMapColor;
varying vec3 worldCoord;

void main() {
    vec4 tex = texture2D(bgl_RenderedTexture, texcoord);
	gl_FragColor = gl_Color*tex*vLightMapColor;
	
	vec3 glow = vec3(0.608, 0.757, 0.227);
	float br = getVisualBrightness(gl_FragColor.rgb);
	float t = float(time)*0.01;
	float noiseVal = snoise4d(vec4(worldCoord*0.08, t));
	vec3 edgeDist = getDistanceToBlockEdge(worldCoord);
	float edgeDistSc = min(1.0, max(edgeDist.x, edgeDist.z)+2.5*max(0.0, 1.0-edgeDist.y*99.0));
	float edgeF = max(0.0, 1.0-edgeDistSc*8.0);
	float f = max(0.0, 0.25-abs(noiseVal))*4.0*intensity*edgeF;
	gl_FragColor.rgb += glow*min(1.0, (1.0-pow(br, 0.1))*f*3.0);
}