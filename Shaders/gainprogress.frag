//uniform float density; //ie how many "real" pixels per display pixel (pixelation level)

void main() {
	vec2 texUV = texcoord;
	float density = 1.0+intensity*4.0;
	texUV.x = roundToNearest(texUV.x, density/(float(screenWidth)));
	texUV.y = roundToNearest(texUV.y, density/(float(screenHeight)));
	vec4 color = texture2D(bgl_RenderedTexture, texUV);
    gl_FragColor = vec4(color.x, color.y, color.z, color.a);
}