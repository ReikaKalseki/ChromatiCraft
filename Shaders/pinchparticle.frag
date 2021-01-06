#import math
#import geometry

uniform float distance;

uniform float scale;
uniform float clip;

uniform float dx;
uniform float dy;
uniform float dz;

float scaleFactor(float dist) {
    return 1.0+min(3.0, intensity*scale*0.05/(distance*dist));
}

void main() {
	vec2 particleXY = getScreenPos(dx, dy, dz);	
	float distv = distsq(particleXY, texcoord);
	float distfac_vertex = max(0.0, min(1.0, clip-15.0*distv*distance/scale));
	float vf = intensity*distfac_vertex;
	
	//vec2 texUV = mix(texcoord, particleXY, vf);
	
	vec2 diff = texcoord-particleXY;
	diff *= scaleFactor(distv);
	vec2 texUV = particleXY+diff;
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	//color.rgb = mix(color.rgb, vec3(1.0, 0.0, 0.0), vf);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}