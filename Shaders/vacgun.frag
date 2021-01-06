#import math
#import geometry

uniform float distance;
uniform float size;

float scaleFactor(float dist) {
    return 1.0+intensity*min(1.0, distance/5.0)*max(0.0, min(1.0, max(0.0, 18.0/distance))*min(6.0, 0.08/(dist*distance/size)));
}

void main() {
	vec2 focusXY = getScreenPos(0.0, 0.5, 0.0);
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
	
	float distv = distsq(focusXY, texcoord);
	
	vec2 diff = texcoord-focusXY;
	diff *= scaleFactor(distv);
	vec2 texUV = focusXY+diff;
	
    color = texture2D(bgl_RenderedTexture, texUV);    
    gl_FragColor = color;
}