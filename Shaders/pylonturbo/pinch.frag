#import math
#import geometry

void main() {
	vec2 focusXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distsq = distsq(focusXY, texcoord);
	
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}