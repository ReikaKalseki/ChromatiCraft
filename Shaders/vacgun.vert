void main() {
    vec4 vert = gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * vert;
    texcoord = vec2(gl_MultiTexCoord0);
	/*	
	vec3 ndc = vert.xyz / vert.w;
	vec2 vertXY = ((ndc.xy + 1.0) / 2.0);
	*/
	/*
	vec4 clipSpacePos = projection * (modelview * vec4(0, 0, 0, 1.0));
	vec3 ndcSpacePos = clipSpacePos.xyz / clipSpacePos.w;
	vec2 monsterXY = ((ndcSpacePos.xy + 1.0) / 2.0);
	
	float dist = dist(monsterXY, texcoord);
	float distfac = max(0.0, 1.0-3.5*dist*dist);
	float bf = intensity*distfac;
	*/
	/*
	vec3 net = mix(vert.xyz, vec3(vert.x, vert.y+0.6, vert.z), bf);
	vert.xyz = net;
	
	vertXY = mix(vertXY, monsterXY, bf);
    
    gl_Position.xyz = gl_ModelViewProjectionMatrix * vert;
	*/
	//texcoord = mix(texcoord, monsterXY, bf);
}