void main() {
	vec2 focusXY = getScreenPos(0.5, 0.5, 0.5);
	
	float distsq = distsq(focusXY, texcoord);
	
	vec2 texUV = texcoord.xy;
    vec4 color = texture2D(bgl_RenderedTexture, texUV);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}