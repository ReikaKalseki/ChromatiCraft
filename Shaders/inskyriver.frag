void main() {
	vec4 clipSpacePos = projection * (modelview * vec4(0.5, 0.5, 0.5, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 nodeXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float distsq = distsq(nodeXY, texcoord);
	
	
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);

    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}