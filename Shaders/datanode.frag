#import math
#import geometry
#import color

void main() {
	vec2 nodeXY = getScreenPos(0, 3.875, 0);
	
	float distv = distsq(nodeXY, texcoord);
	float distfac_color = max(0.0, min(1.0, 1.0-15.0*distv));
	//float distfac_color = max(0.0, min(1.0, 1.0-50.0*distv));
	//float distfac_color2 = max(0.0, min(1.0, 1.0-150.0*distv));
	float distfac_vertex = max(0.0, min(1.0, 1.5-15.0*distv));
	float cf = intensity*distfac_color*0.8;
	//float cf = intensity*distfac_color*0.75;
	//float cf2 = intensity*distfac_color2*0.5;
	float vf = intensity*distfac_vertex*0.006;
	
	vec2 texUV = texcoord.xy;
	texUV.x += 0.11*vf*sin(23.3+texUV.y*111.8+float(time)/2.1);
	texUV.y += 0.19*vf*sin(34.5+texUV.x*115.7+float(time)/2.3);
	
    vec4 color = texture2D(bgl_RenderedTexture, texUV);
	vec3 hsb = rgb2hsb(color.xyz);
	hsb.z = min(1.0, hsb.z+0.125);
	vec3 c2 = hsb2rgb(hsb);
	c2.y = min(1.0, c2.y*1.1+0.035);
	c2.z = min(1.0, c2.z*1.25+0.125);
	color.rgb = mix(color.rgb, c2, cf);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}