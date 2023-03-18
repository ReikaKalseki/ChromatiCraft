#import color
#import noise

vec4 getGrayscaledPixel(vec2 coord, float w, float h)
{
	vec4 color = texture2D(bgl_RenderedTexture, coord);
	color += texture2D(bgl_RenderedTexture, coord+vec2(w, 0));
	color += texture2D(bgl_RenderedTexture, coord-vec2(w, 0));
	color += texture2D(bgl_RenderedTexture, coord+vec2(0, h));
	color += texture2D(bgl_RenderedTexture, coord-vec2(0, h));
	color *= 0.2;
	float br = getVisualBrightness(color.rgb);
	return vec4(br, br, br, 1.0);
}

void make_kernel(inout vec4 n[9], vec2 coord)
{
	float w = 1.0 / float(screenWidth);
	float h = 1.0 / float(screenHeight);

	n[0] = getGrayscaledPixel(coord + vec2( -w, -h), w, h);
	n[1] = getGrayscaledPixel(coord + vec2(0.0, -h), w, h);
	n[2] = getGrayscaledPixel(coord + vec2(  w, -h), w, h);
	n[3] = getGrayscaledPixel(coord + vec2( -w, 0.0), w, h);
	n[4] = getGrayscaledPixel(coord, w, h);
	n[5] = getGrayscaledPixel(coord + vec2(  w, 0.0), w, h);
	n[6] = getGrayscaledPixel(coord + vec2( -w, h), w, h);
	n[7] = getGrayscaledPixel(coord + vec2(0.0, h), w, h);
	n[8] = getGrayscaledPixel(coord + vec2(  w, h), w, h);
}

void main() 
{
	vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
	vec4 n[9];
	make_kernel( n, texcoord );

	vec4 sobel_edge_h = n[2] + (2.0*n[5]) + n[8] - (n[0] + (2.0*n[3]) + n[6]);
  	vec4 sobel_edge_v = n[0] + (2.0*n[1]) + n[2] - (n[6] + (2.0*n[7]) + n[8]);
	vec4 sobel = sqrt((sobel_edge_h * sobel_edge_h) + (sobel_edge_v * sobel_edge_v));

	float dT = float(time)*0.01;
	vec3 glow = min(sobel.rgb, sobel.rgb-vec3(0.985+snoise(vec2(screenWidth, screenHeight)*texcoord*vec2(0.005, 0.0008)+vec2(0, -dT))));
	vec3 add = sqrt(sqrt(max(vec3(0.0), glow)))*vec3(0.608, 0.757, 0.227)*min(1.0, intensity*1.2);
	vec3 net = orig.rgb+add;
    
    gl_FragColor = vec4(min(1.0, net.x), min(1.0, net.y), min(1.0, net.z), orig.a);
}
